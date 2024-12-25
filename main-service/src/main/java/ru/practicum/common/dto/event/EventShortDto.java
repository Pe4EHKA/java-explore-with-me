package ru.practicum.common.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.common.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
public class EventShortDto {

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Long id;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;

    private int commentsSize;
}
