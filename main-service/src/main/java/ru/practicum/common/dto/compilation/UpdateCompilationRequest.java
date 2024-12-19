package ru.practicum.common.dto.compilation;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationRequest {

    private Set<Long> events;

    private Boolean pinned;

    @Length(min = 1, max = 50)
    private String title;
}
