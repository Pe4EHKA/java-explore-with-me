package ru.practicum.common.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

    private Long id;

    private Long eventId;

    private Long userId;

    @NotBlank
    @Length(min = 1, max = 2048)
    private String text;
}
