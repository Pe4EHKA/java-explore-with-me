package ru.practicum.common.service;

import ru.practicum.common.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequests(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto updateRequest(Long userId, Long requestId);
}
