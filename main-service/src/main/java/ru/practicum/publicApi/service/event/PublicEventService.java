package ru.practicum.publicApi.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.common.dto.event.EventFullDto;
import ru.practicum.common.dto.event.EventShortDto;
import ru.practicum.publicApi.dto.RequestParamForEvent;

import java.util.Set;

public interface PublicEventService {

    Set<EventShortDto> getAllEvents(RequestParamForEvent requestParamForEvent);

    EventFullDto getEvent(Long eventId, HttpServletRequest httpServletRequest);
}
