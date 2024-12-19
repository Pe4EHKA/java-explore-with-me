package ru.practicum.privateApi.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.dto.event.*;
import ru.practicum.common.dto.request.ParticipationRequestDto;
import ru.practicum.common.enums.State;
import ru.practicum.common.enums.Status;
import ru.practicum.common.enums.UserAction;
import ru.practicum.common.exception.BadRequestException;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.common.mapper.EventMapper;
import ru.practicum.common.mapper.PropertyMerger;
import ru.practicum.common.mapper.RequestMapper;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.Request;
import ru.practicum.common.repository.CategoryRepository;
import ru.practicum.common.repository.RequestRepository;
import ru.practicum.common.repository.UserRepository;
import ru.practicum.common.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

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

        return EventMapper.toEventFullDto(event);
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
        event.setViews(0L);

        try {
            event = eventRepository.save(event);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }

        log.info("Created Event: {}", event);

        return EventMapper.toEventFullDto(event);
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

        if (State.PUBLISHED.toString().equals(eventOld.getState().toString())) {
            throw new ConflictException("Event is published and cannot be updated");
        }

        PropertyMerger.mergeProperties(eventNew, eventOld);

        if (updateEventUserRequest.getStateAction() != null) {
            if (UserAction.CANCEL_REVIEW.toString().equals(updateEventUserRequest.getStateAction().toString())) {
                eventOld.setState(State.CANCELED);
            } else if (UserAction.SEND_TO_REVIEW.toString().equals(updateEventUserRequest.getStateAction().toString())) {
                eventOld.setState(State.PENDING);
            }
        }

        try {
            eventOld = eventRepository.save(eventOld);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }
        log.info("Updated Event: {}", eventOld);

        return EventMapper.toEventFullDto(eventOld);
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
}
