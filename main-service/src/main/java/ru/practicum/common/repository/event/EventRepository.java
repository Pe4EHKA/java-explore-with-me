package ru.practicum.common.repository.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.practicum.common.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, EventSearchRepository {

    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);

    Set<Event> findAllByIdIn(Set<Long> ids);

    Boolean existsByCategoryId(Long catId);

    @Query("select event " +
            "from Event as event " +
            "where (?1 is null or event.initiator.id in ?1) " +
            "and  (?2 is null or event.category.id  in ?2) " +
            "and (?3 is null or event.state in ?3) " +
            "and (cast(?4 as timestamp) is null or event.eventDate >= ?4) " +
            "and (cast(?5 as timestamp) is null or event.eventDate <= ?5) ")
    List<Event> findWithParams(List<Long> users,
                               List<Long> categories,
                               List<String> states,
                               LocalDateTime startTime,
                               LocalDateTime endTime,
                               Pageable pageable);
}
