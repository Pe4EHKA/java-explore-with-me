package ru.practicum.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.event.EventFullDto;
import ru.practicum.common.dto.event.EventShortDto;
import ru.practicum.common.service.EventService;
import ru.practicum.api.dto.PublicRequestParamForEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {

    public final EventService eventService;

    @GetMapping
    public Set<EventShortDto> getAllEvents(@RequestParam(required = false) String text,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) Boolean paid,
                                           @RequestParam(required = false) LocalDateTime rangeStart,
                                           @RequestParam(required = false) LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                           @RequestParam(defaultValue = "event_date") String sort,
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           HttpServletRequest httpServletRequest) {
        PublicRequestParamForEvent publicRequestParamForEvent = PublicRequestParamForEvent.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .request(httpServletRequest)
                .build();

        log.info("Get all events with params: {}", publicRequestParamForEvent);

        return eventService.getAllEvents(publicRequestParamForEvent);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable("id") Long id,
                                 HttpServletRequest httpServletRequest) {
        log.info("Get event with id {}", id);
        return eventService.getEvent(id, httpServletRequest);
    }
}
