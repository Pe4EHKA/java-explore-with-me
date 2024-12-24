package ru.practicum.common.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.common.dto.comment.CommentDto;
import ru.practicum.common.dto.comment.NewCommentDto;
import ru.practicum.common.model.Comment;
import ru.practicum.common.model.Event;
import ru.practicum.common.model.User;

import java.util.List;

@UtilityClass
public class CommentMapper {

    public List<CommentDto> toCommentDtos(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getId())
                .userId(comment.getUser().getId())
                .text(comment.getText())
                .build();
    }

    public Comment toComment(NewCommentDto newCommentDto, Event event, User user) {
        return Comment.builder()
                .event(event)
                .user(user)
                .text(newCommentDto.getText())
                .build();
    }

    public Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }
}
