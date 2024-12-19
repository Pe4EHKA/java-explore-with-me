package ru.practicum.privateApi.service.event;

import ru.practicum.common.dto.event.*;
import ru.practicum.common.dto.request.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {
    List<EventShortDto> getAllEvents(Long userId, Integer from, Integer size);

    EventFullDto getEvent(Long userId, Long eventId);

    List<ParticipationRequestDto> getParticipation(Long userId, Long eventId);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest updateEventStatusRequest);
}
