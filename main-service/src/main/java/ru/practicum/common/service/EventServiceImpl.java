package ru.practicum.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.api.dto.AdminRequestParamForEvent;
import ru.practicum.api.dto.PublicRequestParamForEvent;
import ru.practicum.client.StatClient;
import ru.practicum.common.dto.event.*;
import ru.practicum.common.dto.request.ParticipationRequestDto;
import ru.practicum.common.enums.AdminAction;
import ru.practicum.common.enums.State;
import ru.practicum.common.enums.Status;
import ru.practicum.common.enums.UserAction;
import ru.practicum.common.exception.BadRequestException;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.common.mapper.EventMapper;
import ru.practicum.common.mapper.PropertyMerger;
import ru.practicum.common.mapper.RequestMapper;
import ru.practicum.common.model.Category;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.EventSearch;
import ru.practicum.common.model.Request;
import ru.practicum.common.repository.CategoryRepository;
import ru.practicum.common.repository.RequestRepository;
import ru.practicum.common.repository.UserRepository;
import ru.practicum.common.repository.event.EventRepository;
import ru.practicum.dto.CreateHitDto;
import ru.practicum.dto.HitStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final String serviceName = "ewm-main-service";

    private final ObjectMapper objectMapper;

    private final StatClient statClient;

    @Override
    public List<EventShortDto> getAllEvents(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<EventShortDto> eventShortDtos = EventMapper
                .eventShortDtoList(eventRepository.findAll(pageRequest).toList());
        log.info("Got All Events for user: {}, events size: {}", userId, eventShortDtos.size());

        return eventShortDtos;
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        Event event = eventRepository
                .findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() ->
                        new NotFoundException("Event not found with id: " + eventId + "and userId: " + userId));
        log.info("Got Event: {}", event);

        return EventMapper.toEventFullDto(event, null);
    }

    @Override
    public List<ParticipationRequestDto> getParticipation(Long userId, Long eventId) {
        eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() ->
                        new NotFoundException("Event not found with id: " + eventId + "and userId: " + userId));
        log.info("Got Participations for user: {}, event: {}", userId, eventId);

        return RequestMapper.toParticipationRequestDtoList(requestRepository.findAllByEventId(eventId));
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        validEventDate(newEventDto.getEventDate());

        Event event = EventMapper.toEvent(newEventDto);
        event.setCategory(categoryRepository
                .findById(newEventDto.getCategory()).orElseThrow(() ->
                        new NotFoundException(String.format("Category with id: %d not found", newEventDto.getCategory()))));

        event.setPublishedOn(LocalDateTime.now());
        event.setInitiator(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id: %d not found", userId))));

        try {
            event = eventRepository.save(event);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }

        log.info("Created Event: {}", event);

        return EventMapper.toEventFullDto(event, 0L);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event eventOld = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() ->
                        new NotFoundException("Event not found with id: " + eventId + "and userId: " + userId));
        Event eventNew = EventMapper.toEvent(updateEventUserRequest);

        validEventDate(eventNew.getEventDate());

        if (updateEventUserRequest.getCategory() != null) {
            eventNew.setCategory(categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException(String
                            .format("Category with id: %d not found", updateEventUserRequest.getCategory()))));
        }

        if (State.PUBLISHED.equals(eventOld.getState())) {
            throw new ConflictException("Event is published and cannot be updated");
        }

        PropertyMerger.mergeProperties(eventNew, eventOld);

        if (updateEventUserRequest.getStateAction() != null) {
            if (UserAction.CANCEL_REVIEW.equals(updateEventUserRequest.getStateAction())) {
                eventOld.setState(State.CANCELED);
            } else if (UserAction.SEND_TO_REVIEW.equals(updateEventUserRequest.getStateAction())) {
                eventOld.setState(State.PENDING);
            }
        }

        try {
            eventOld = eventRepository.save(eventOld);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }
        log.info("Updated Event: {}", eventOld);

        return EventMapper.toEventFullDto(eventOld, getViewsEvent("/events/" + eventOld.getId()));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequest
                                                                      updateEventStatusRequest) {
        List<Long> requestIds = updateEventStatusRequest.getRequestIds();
        List<Request> requests = requestRepository.findAllByIdIn(requestIds);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() ->
                        new NotFoundException("Event not found with id: " + eventId + "and userId: " + userId));

        String status = updateEventStatusRequest.getStatus();

        if (Status.REJECTED.toString().equals(status)) {
            return handleRejectedRequests(requests);
        }

        if (Status.CONFIRMED.toString().equals(status)) {
            return handleConfirmedRequests(requests, event, requestIds.size());
        }

        throw new ConflictException("Invalid request status: " + status);
    }

    @Override
    public List<EventFullDto> getALlEvents(AdminRequestParamForEvent param) {
        Pageable pageable = PageRequest
                .of(param.getFrom() / param.getSize(),
                        param.getSize(),
                        Sort.by(Sort.Direction.ASC, "id"));
        List<Event> events = eventRepository.findWithParams(param.getUsers(),
                param.getCategories(),
                param.getStates(),
                param.getRangeStart(),
                param.getRangeEnd(),
                pageable);


        List<EventFullDto> eventFullDtos = events.stream()
                .map(event -> EventMapper.toEventFullDto(event, null))
                .toList();


        log.info("Got events {} for params {}", events, param);
        return eventFullDtos;
    }

    @Override
    @Transactional
    public EventFullDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event current = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id: %d not found", eventId)));

        if (State.PUBLISHED.equals(current.getState())) {
            throw new ConflictException(String.format("Event with id: %d is already PUBLISHED", eventId));
        }
        if (State.CANCELED.equals(current.getState())) {
            throw new ConflictException(String.format("Event with id: %d is already CANCELED", eventId));
        }

        PropertyMerger.mergeProperties(EventMapper.toEvent(updateEventAdminRequest), current);

        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository
                    .findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException(String
                            .format("Category with id: %d not found", updateEventAdminRequest.getCategory())));
            current.setCategory(category);
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            validEventDate(updateEventAdminRequest.getEventDate());
            current.setEventDate(updateEventAdminRequest.getEventDate());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (AdminAction.PUBLISH_EVENT.equals(updateEventAdminRequest.getStateAction())) {
                current.setState(State.PUBLISHED);
            } else if (AdminAction.REJECT_EVENT.equals(updateEventAdminRequest.getStateAction())) {
                current.setState(State.CANCELED);
            }
        }

        try {
            current = eventRepository.save(current);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }
        log.info("Event updated: {}", current);
        return EventMapper.toEventFullDto(current, null);
    }

    @Override
    public Set<EventShortDto> getAllEvents(PublicRequestParamForEvent publicRequestParamForEvent) {
        LocalDateTime rangeStart = publicRequestParamForEvent.getRangeStart();
        LocalDateTime rangeEnd = publicRequestParamForEvent.getRangeEnd();

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("rangeStart cannot be after rangeEnd");
        }

        PageRequest pageRequest = initPageable(publicRequestParamForEvent.getFrom(),
                publicRequestParamForEvent.getSize(), publicRequestParamForEvent.getSort());

        EventSearch eventSearch = initEventSearch(publicRequestParamForEvent);

        Set<EventShortDto> eventShortDtos = EventMapper.toEventShortDtoSet(eventRepository
                .findWithFilter(eventSearch, pageRequest).toSet());

        List<String> uris = eventShortDtos.stream()
                .map(event -> "/events/" + event.getId())
                .toList();

        Map<Long, Long> views = getViewsEvents(uris);

        for (EventShortDto eventDto : eventShortDtos) {
            eventDto.setViews(views.get(eventDto.getId()));
        }

        log.info("Got events {}", eventShortDtos);
        hitEndpoint(publicRequestParamForEvent.getRequest());

        return eventShortDtos;
    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id %s not found", eventId)));

        if (!State.PUBLISHED.equals(event.getState())) {
            throw new NotFoundException(String.format("Event with id %s is not published", eventId));
        }

        long views = getViewsEvent(httpServletRequest.getRequestURI());

        hitEndpoint(httpServletRequest);

        log.info("Got event {}", event);

        return EventMapper.toEventFullDto(event, views);
    }

    private Map<Long, Long> getViewsEvents(List<String> uris) {
        Map<Long, Long> views = new HashMap<>();

        ResponseEntity<?> response = statClient.getStats(LocalDateTime.now().minusYears(100).toString(),
                LocalDateTime.now().plusYears(100).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                uris,
                true);

        HitStatsDto[] stats = new HitStatsDto[0];

        try {
            stats = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()),
                    HitStatsDto[].class);
        } catch (Exception e) {
            log.error("Failed to parse stats response", e);
        }

        for (HitStatsDto stat : stats) {
            String uri = stat.getUri();
            views.put(Long.parseLong(uri.substring(uri.lastIndexOf('/'))), stat.getHits());
        }

        return views;
    }

    private Long getViewsEvent(String request) {
        ResponseEntity<?> response = statClient.getStats(LocalDateTime.now().minusYears(100).toString(),
                LocalDateTime.now().plusYears(100).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                List.of(request),
                true);

        long hits = 0L;

        try {
            if (response.getBody() == null || response.getBody().toString().equals("[]")) {
                return 0L;
            }

            HitStatsDto[] stats = objectMapper.readValue(response.getBody().toString(),
                    HitStatsDto[].class);

            if (stats.length > 0) {
                hits = stats[0].getHits();
            }

        } catch (Exception e) {
            log.error("Failed to parse stats response", e);
        }

        return hits;
    }

    private EventRequestStatusUpdateResult handleRejectedRequests(List<Request> requests) {
        if (requests.stream()
                .anyMatch(request -> request.getStatus().equals(Status.CONFIRMED))) {
            throw new ConflictException("Can't reject confirmed requests");
        }

        List<ParticipationRequestDto> rejectedRequests = requests.stream()
                .peek(request -> request.setStatus(Status.REJECTED))
                .map(RequestMapper::toParticipationRequestDto)
                .toList();

        return new EventRequestStatusUpdateResult(List.of(), rejectedRequests);
    }

    private EventRequestStatusUpdateResult handleConfirmedRequests(List<Request> requests,
                                                                   Event event,
                                                                   long requestSize) {
        Long participantLimit = event.getParticipantLimit();
        Long approvedRequests = event.getConfirmedRequests();
        long availableParticipant = participantLimit - approvedRequests;

        if (participantLimit > 0 && participantLimit.equals(approvedRequests)) {
            throw new ConflictException("Participant limit exceeded, event with id: " + event.getId());
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        if (participantLimit == 0 || (!event.getRequestModeration() && requestSize <= availableParticipant)) {
            confirmedRequests = processRequests(requests, Status.CONFIRMED);
            event.setConfirmedRequests(approvedRequests + requestSize);
        } else {
            confirmedRequests = processRequests(requests.stream()
                    .limit(availableParticipant)
                    .toList(), Status.CONFIRMED);
            rejectedRequests = processRequests(requests.stream()
                    .skip(availableParticipant)
                    .toList(), Status.REJECTED);
            event.setConfirmedRequests(approvedRequests + confirmedRequests.size());
        }

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private List<ParticipationRequestDto> processRequests(List<Request> requests, Status status) {
        return requests.stream()
                .peek(request -> {
                    if (!request.getStatus().equals(status)) {
                        request.setStatus(status);
                    } else {
                        throw new ConflictException("Request with id: " + request.getId() + " already " + status);
                    }
                })
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    private void validEventDate(LocalDateTime eventDate) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(1).plusSeconds(10))) {
            throw new BadRequestException("Event date cannot be in the past " +
                    "and earlier than current time in 2 hours");
        }
    }

    private void hitEndpoint(HttpServletRequest request) {
        CreateHitDto createHitDto = CreateHitDto.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app(serviceName)
                .timestamp(LocalDateTime.now())
                .build();
        statClient.hit(createHitDto);
    }

    private EventSearch initEventSearch(PublicRequestParamForEvent publicRequestParamForEvent) {
        return EventSearch.builder()
                .text(publicRequestParamForEvent.getText())
                .categories(publicRequestParamForEvent.getCategories())
                .rangeStart(publicRequestParamForEvent.getRangeStart())
                .rangeEnd(publicRequestParamForEvent.getRangeEnd())
                .paid(publicRequestParamForEvent.getPaid())
                .onlyAvailable(publicRequestParamForEvent.getOnlyAvailable())
                .build();
    }

    private PageRequest initPageable(int from, int size, String sort) {
        PageRequest pageRequest = null;

        if (sort == null || "event_date".equalsIgnoreCase(sort)) {
            pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "event_date"));
        } else if ("views".equalsIgnoreCase(sort)) {
            pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "views"));
        }
        return pageRequest;
    }
}
