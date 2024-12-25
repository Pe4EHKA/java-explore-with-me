package ru.practicum.common.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.common.dto.location.LocationDto;
import ru.practicum.common.dto.user.UserShortDto;
import ru.practicum.common.enums.State;

import java.time.LocalDateTime;

@Data
@Builder
public class EventFullDto {

    private long id;

    private String annotation;

    private CategoryDto category;

    private long confirmedRequests;

    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private LocationDto location;

    private boolean paid;

    private long participantLimit;

    private LocalDateTime publishedOn;

    private boolean requestModeration;

    private State state;

    private String title;

    private Long views;

    private int commentsSize;
}
