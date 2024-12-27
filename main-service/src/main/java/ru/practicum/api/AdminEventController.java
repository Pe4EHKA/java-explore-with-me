package ru.practicum.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.dto.AdminRequestParamForEvent;
import ru.practicum.common.dto.event.EventFullDto;
import ru.practicum.common.dto.event.UpdateEventAdminRequest;
import ru.practicum.common.service.CommentService;
import ru.practicum.common.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class AdminEventController {
    private final EventService adminEventService;

    private final CommentService adminCommentService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<String> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) LocalDateTime rangeStart,
                                        @RequestParam(required = false) LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET Admin events request");

        AdminRequestParamForEvent adminRequestParamForEvent = AdminRequestParamForEvent.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        return adminEventService.getAllEvents(adminRequestParamForEvent);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                               @PathVariable(name = "eventId") Long eventId) {
        log.info("PUT Admin event: {}", updateEventAdminRequest);
        return adminEventService.update(eventId, updateEventAdminRequest);
    }

    @DeleteMapping("/{eventId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(name = "eventId") Long eventId,
                       @PathVariable(name = "commentId") Long commentId) {
        log.info("DELETE Admin eventId: {} commentId: {}", eventId, commentId);

        adminCommentService.deleteComment(commentId);
    }
}
