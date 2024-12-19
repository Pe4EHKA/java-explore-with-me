package ru.practicum.publicApi.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatClient;
import ru.practicum.common.dto.event.EventFullDto;
import ru.practicum.common.dto.event.EventShortDto;
import ru.practicum.common.enums.State;
import ru.practicum.common.exception.BadRequestException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.common.mapper.EventMapper;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.EventSearch;
import ru.practicum.common.repository.event.EventRepository;
import ru.practicum.dto.CreateHitDto;
import ru.practicum.publicApi.dto.RequestParamForEvent;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PublicEventServiceImpl implements PublicEventService {

    private final String serviceName = "ewm-main-service";

    private final EventRepository eventRepository;

    private final StatClient statClient;

    @Override
    public Set<EventShortDto> getAllEvents(RequestParamForEvent requestParamForEvent) {
        LocalDateTime rangeStart = requestParamForEvent.getRangeStart();
        LocalDateTime rangeEnd = requestParamForEvent.getRangeEnd();

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("rangeStart cannot be after rangeEnd");
        }

        PageRequest pageRequest = initPageable(requestParamForEvent.getFrom(),
                requestParamForEvent.getSize(), requestParamForEvent.getSort());

        EventSearch eventSearch = initEventSearch(requestParamForEvent);

        Set<EventShortDto> eventShortDtos = EventMapper.toEventShortDtoSet(eventRepository
                .findWithFilter(eventSearch, pageRequest).toSet());

        log.info("Got events {}", eventShortDtos);
        hitEndpoint(requestParamForEvent.getRequest());

        return eventShortDtos;
    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id %s not found", eventId)));

        if (!State.PUBLISHED.equals(event.getState())) {
            throw new NotFoundException(String.format("Event with id %s is not published", eventId));
        }

        event.setViews(event.getViews() + 1);
        eventRepository.save(event);

        hitEndpoint(httpServletRequest);

        log.info("Got event {}", event);

        return EventMapper.toEventFullDto(event);
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

    private EventSearch initEventSearch(RequestParamForEvent requestParamForEvent) {
        return EventSearch.builder()
                .text(requestParamForEvent.getText())
                .categories(requestParamForEvent.getCategories())
                .rangeStart(requestParamForEvent.getRangeStart())
                .rangeEnd(requestParamForEvent.getRangeEnd())
                .paid(requestParamForEvent.getPaid())
                .onlyAvailable(requestParamForEvent.getOnlyAvailable())
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
