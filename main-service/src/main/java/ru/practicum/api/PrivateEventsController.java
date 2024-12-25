package ru.practicum.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.comment.CommentDto;
import ru.practicum.common.dto.comment.NewCommentDto;
import ru.practicum.common.dto.event.*;
import ru.practicum.common.dto.request.ParticipationRequestDto;
import ru.practicum.common.enums.Status;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.service.CommentService;
import ru.practicum.common.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class PrivateEventsController {

    private final EventService privateEventService;

    private final CommentService privateCommentService;

    @GetMapping
    public List<EventShortDto> getAllEvents(@PathVariable("userId") Long userId,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET Private Events [userId={}, from={}, size={}]", userId, from, size);
        return privateEventService.getAllEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable("userId") Long userId,
                                 @PathVariable("eventId") Long eventId) {
        log.info("GET Private Event [userId={}, eventId={}]", userId, eventId);
        return privateEventService.getEvent(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable("userId") Long userId,
                                                     @PathVariable("eventId") Long eventId) {
        log.info("GET Private Requests [userId={}, eventId={}]", userId, eventId);
        return privateEventService.getParticipation(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("POST Private Event [userId={}, event={}]", userId, newEventDto);
        return privateEventService.createEvent(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable("userId") Long userId,
                                    @PathVariable("eventId") Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("PATCH Private Event [userId={}, eventId={}, updateEventUserRequest={}]",
                userId, eventId, updateEventUserRequest);
        return privateEventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable("userId") Long userId,
                                                              @PathVariable("eventId") Long eventId,
                                                              @Valid @RequestBody EventRequestStatusUpdateRequest
                                                                      eventRequestStatusUpdateRequest) {
        log.info("PATCH Private Request Status [userId={}, eventId={}, eventRequestStatusUpdateRequesr={}]",
                userId, eventId, eventRequestStatusUpdateRequest);

        if (Status.from(eventRequestStatusUpdateRequest.getStatus()) == null) {
            throw new ConflictException("Check status. Status validate exception");
        }
        return privateEventService.updateRequestStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @PostMapping("/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto saveComment(@PathVariable("eventId") Long eventId,
                                  @PathVariable("userId") Long userId,
                                  @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("POST Private Event Comment [eventId={}, userId={}, newCommentDto={}]",
                eventId, userId, newCommentDto);
        return privateCommentService.saveComment(eventId, userId, newCommentDto);
    }

    @PatchMapping("/{eventId}/comments")
    public CommentDto updateComment(@PathVariable("eventId") Long eventId,
                                    @PathVariable("userId") Long userId,
                                    @RequestBody CommentDto commentDto) {
        log.info("PATCH Private Event Comment [eventId={}, userId={}, commentDto={}]",
                eventId, userId, commentDto);

        return privateCommentService.updateComment(eventId, userId, commentDto);
    }

    @DeleteMapping("/{eventId}/comments")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("eventId") Long eventId,
                              @PathVariable("userId") Long userId) {
        log.info("DELETE Private Event Comment [eventId={}, userId={}]", eventId, userId);

        privateCommentService.deleteComment(eventId, userId);
    }
}
