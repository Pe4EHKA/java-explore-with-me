package ru.practicum.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.dto.comment.CommentDto;
import ru.practicum.common.dto.comment.NewCommentDto;
import ru.practicum.common.enums.Status;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.common.mapper.CommentMapper;
import ru.practicum.common.model.Comment;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.Request;
import ru.practicum.common.repository.CommentRepository;
import ru.practicum.common.repository.RequestRepository;
import ru.practicum.common.repository.event.EventRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    private final CommentRepository commentRepository;

    @Override
    public List<CommentDto> getAllComments(Long eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id %s not found", eventId)));

        List<Comment> comments = commentRepository.findAllByEventId(eventId);

        log.info("Got all comments of event, count: {}", comments.size());
        return CommentMapper.toCommentDtos(comments);
    }

    @Override
    public CommentDto getComment(Long eventId, Long commentId) {
        Comment comment = commentRepository.findByEventIdAndId(eventId, commentId)
                .orElseThrow(() -> new NotFoundException(String
                        .format("Comment with event Id: %s and comment Id: %s not found", eventId, commentId)));

        log.info("Got comment {}", comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto saveComment(Long eventId, Long userId, NewCommentDto newCommentDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id %s not found", eventId)));

        Request request = requestRepository.findByRequesterIdAndEventIdAndStatus(userId, eventId, Status.CONFIRMED)
                .orElseThrow(() -> new NotFoundException(String
                        .format("User with id %s is not a Participant of event with id %s", userId, eventId)));

        Comment comment = CommentMapper.toComment(newCommentDto, event, request.getRequester());

        commentRepository.save(comment);
        commentRepository.flush();

        log.info("Comment saved: {}", comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long eventId, Long userId, CommentDto commentDto) {
        Comment comment = commentRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Comment with event id " + eventId +
                        " and user id " + userId + " not found"));

        comment.setText(commentDto.getText() != null ? commentDto.getText() : comment.getText());

        log.info("Update comment {}", comment);

        commentRepository.save(comment);
        commentRepository.flush();

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long eventId, Long userId) {
        Comment comment = commentRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Comment with event id " + eventId +
                        " and user id " + userId + " not found"));

        commentRepository.deleteById(comment.getId());
        commentRepository.flush();

        log.info("Comment deleted: {}", comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
        commentRepository.flush();

        log.info("Admin: Comment deleted: {}", commentId);
    }
}
