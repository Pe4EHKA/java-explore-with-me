package ru.practicum.adminApi.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.adminApi.dto.RequestParamForEvent;
import ru.practicum.adminApi.service.event.AdminEventService;
import ru.practicum.common.dto.event.EventFullDto;
import ru.practicum.common.dto.event.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class AdminEventController {
    private final AdminEventService adminEventService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<String> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) LocalDateTime rangeStart,
                                        @RequestParam(required = false) LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET Admin events request");

        RequestParamForEvent requestParamForEvent = RequestParamForEvent.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        return adminEventService.getALlEvents(requestParamForEvent);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                               @PathVariable(name = "eventId") Long eventId) {
        log.info("PUT Admin event: {}", updateEventAdminRequest);
        return adminEventService.update(eventId, updateEventAdminRequest);
    }
}
