package ru.practicum.common.repository.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.EventSearch;

public interface EventSearchRepository {
    Page<Event> findWithFilter(EventSearch eventSearch, Pageable pageable);
}
