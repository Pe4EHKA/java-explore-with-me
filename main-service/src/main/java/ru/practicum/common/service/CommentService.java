package ru.practicum.common.service;

import ru.practicum.common.dto.comment.CommentDto;
import ru.practicum.common.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> getAllComments(Long eventId);

    CommentDto getComment(Long eventId, Long commentId);

    CommentDto saveComment(Long eventId, Long userId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long eventId, Long userId, CommentDto commentDto);

    void deleteComment(Long eventId, Long userId);

    void deleteComment(Long commentId);

}
