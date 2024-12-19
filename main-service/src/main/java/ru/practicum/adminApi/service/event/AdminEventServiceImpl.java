package ru.practicum.adminApi.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.adminApi.dto.RequestParamForEvent;
import ru.practicum.common.dto.event.EventFullDto;
import ru.practicum.common.dto.event.UpdateEventAdminRequest;
import ru.practicum.common.enums.AdminAction;
import ru.practicum.common.enums.State;
import ru.practicum.common.exception.BadRequestException;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.common.mapper.EventMapper;
import ru.practicum.common.mapper.PropertyMerger;
import ru.practicum.common.model.Category;
import ru.practicum.common.model.Event;
import ru.practicum.common.repository.CategoryRepository;
import ru.practicum.common.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public List<EventFullDto> getALlEvents(RequestParamForEvent param) {
        Pageable pageable = PageRequest
                .of(param.getFrom() / param.getSize(), param.getSize(), Sort.by(Sort.Direction.ASC, "id"));
        List<Event> events = eventRepository.findWithParams(param.getUsers(),
                param.getCategories(),
                param.getStates(),
                param.getRangeStart(),
                param.getRangeEnd(),
                pageable);

        log.info("Got events {} for params {}", events, param);
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event current = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id: %d not found", eventId)));

        if (State.PUBLISHED.toString().equals(current.getState().toString())) {
            throw new ConflictException(String.format("Event with id: %d is already PUBLISHED", eventId));
        }
        if (State.CANCELED.toString().equals(current.getState().toString())) {
            throw new ConflictException(String.format("Event with id: %d is already CANCELED", eventId));
        }

        PropertyMerger.mergeProperties(EventMapper.toEvent(updateEventAdminRequest), current);

        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository
                    .findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException(String
                            .format("Category with id: %d not found", updateEventAdminRequest.getCategory())));
            current.setCategory(category);
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            validEventDate(updateEventAdminRequest.getEventDate());
            current.setEventDate(updateEventAdminRequest.getEventDate());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (AdminAction.PUBLISH_EVENT.toString().equals(updateEventAdminRequest.getStateAction().toString())) {
                current.setState(State.PUBLISHED);
            } else if (AdminAction.REJECT_EVENT.toString().equals(updateEventAdminRequest.getStateAction().toString())) {
                current.setState(State.CANCELED);
            }
        }

        try {
            current = eventRepository.save(current);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }
        log.info("Event updated: {}", current);
        return EventMapper.toEventFullDto(current);
    }

    private void validEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(1).plusSeconds(10))) {
            throw new BadRequestException("Event date cannot be in the past " +
                    "and earlier than current time in 2 hours");
        }
    }

}
