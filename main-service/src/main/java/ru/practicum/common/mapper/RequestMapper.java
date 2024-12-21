package ru.practicum.common.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.common.dto.request.ParticipationRequestDto;
import ru.practicum.common.enums.Status;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.Request;
import ru.practicum.common.model.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class RequestMapper {

    public List<ParticipationRequestDto> toParticipationRequestDtoList(List<Request> requests) {
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    public Request toRequest(User requester, Event event) {
        return Request.builder()
                .requester(requester)
                .event(event)
                .created(LocalDateTime.now())
                .status((event.getRequestModeration() && event.getParticipantLimit() > 0) ?
                        Status.PENDING : Status.CONFIRMED)
                .build();
    }


    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .status(request.getStatus())
                .build();
    }
}
