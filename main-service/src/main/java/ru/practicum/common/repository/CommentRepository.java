package ru.practicum.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.common.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventId(Long eventId);

    Optional<Comment> findByEventIdAndId(Long eventId, Long id);

    Optional<Comment> findByEventIdAndUserId(Long eventId, Long userId);
}
