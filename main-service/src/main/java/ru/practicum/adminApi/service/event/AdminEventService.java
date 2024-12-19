package ru.practicum.adminApi.service.event;

import ru.practicum.adminApi.dto.RequestParamForEvent;
import ru.practicum.common.dto.event.EventFullDto;
import ru.practicum.common.dto.event.UpdateEventAdminRequest;

import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getALlEvents(RequestParamForEvent param);

    EventFullDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
