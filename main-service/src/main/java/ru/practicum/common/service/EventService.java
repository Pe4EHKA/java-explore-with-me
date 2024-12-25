package ru.practicum.common.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.api.dto.AdminRequestParamForEvent;
import ru.practicum.api.dto.PublicRequestParamForEvent;
import ru.practicum.common.dto.comment.CommentDto;
import ru.practicum.common.dto.comment.NewCommentDto;
import ru.practicum.common.dto.event.*;
import ru.practicum.common.dto.request.ParticipationRequestDto;

import java.util.List;
import java.util.Set;

public interface EventService {
    List<EventShortDto> getAllEvents(Long userId, Integer from, Integer size);

    EventFullDto getEvent(Long userId, Long eventId);

    List<ParticipationRequestDto> getParticipation(Long userId, Long eventId);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest updateEventStatusRequest);

    List<EventFullDto> getAllEvents(AdminRequestParamForEvent param);

    EventFullDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    Set<EventShortDto> getAllEvents(PublicRequestParamForEvent publicRequestParamForEvent);

    EventFullDto getEvent(Long eventId, HttpServletRequest httpServletRequest);
}
