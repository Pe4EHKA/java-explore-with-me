package ru.practicum.common.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.common.dto.location.LocationDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class UpdateEventRequest {

    @Length(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Length(min = 20, max = 7000)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @Valid
    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero
    private Long participantLimit;

    private Boolean requestModeration;

    @Length(min = 3, max = 120)
    private String title;
}
