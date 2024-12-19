package ru.practicum.privateApi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.request.ParticipationRequestDto;
import ru.practicum.privateApi.service.request.PrivateRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class PrivateRequestController {

    private final PrivateRequestService privateRequestService;

    @GetMapping
    public List<ParticipationRequestDto> getRequests(@PathVariable("userId") Long userId) {
        log.info("Get requests for requests with user id: {}", userId);
        return privateRequestService.getRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable("userId") Long userId,
                                                 @RequestParam("eventId") Long eventId) {
        log.info("Create request for eventId: {}", eventId);
        return privateRequestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto updateRequest(@PathVariable("userId") Long userId,
                                                 @PathVariable("requestId") Long requestId) {
        log.info("Update request for requestId: {} from userId: {}", requestId, userId);
        return privateRequestService.updateRequest(userId, requestId);

    }
}
