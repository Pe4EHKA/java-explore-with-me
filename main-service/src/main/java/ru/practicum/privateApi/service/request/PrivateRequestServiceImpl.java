package ru.practicum.privateApi.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.dto.request.ParticipationRequestDto;
import ru.practicum.common.enums.State;
import ru.practicum.common.enums.Status;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.common.mapper.RequestMapper;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.Request;
import ru.practicum.common.model.User;
import ru.practicum.common.repository.RequestRepository;
import ru.practicum.common.repository.UserRepository;
import ru.practicum.common.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivateRequestServiceImpl implements PrivateRequestService {

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        return RequestMapper.toParticipationRequestDtoList(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException("Request with requesterId: " + userId +
                    " and eventId: " + eventId + " already exists");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("User with id " + userId + " must not be the same initiator");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Event with id " + eventId + " is not published");
        }
        if (!event.getParticipantLimit().equals(0L) &&
                event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ConflictException("Event with id " + eventId + " is already reached limit of participants");
        }
        if (!event.getRequestModeration()) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        return RequestMapper.toParticipationRequestDto(requestRepository.save(RequestMapper.toRequest(user, event)));
    }

    @Override
    @Transactional
    public ParticipationRequestDto updateRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId +
                        " and requesterId " + userId + " not found"));
        request.setStatus(Status.CANCELED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}
