package ru.practicum.common.dto.comment;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

    private Long id;

    private Long eventId;

    private Long userId;

    private String text;
}
