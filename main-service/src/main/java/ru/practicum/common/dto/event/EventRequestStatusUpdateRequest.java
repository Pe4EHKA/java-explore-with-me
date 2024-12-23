package ru.practicum.common.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateRequest {

    @NotNull
    private List<Long> requestIds;

    @NotBlank
    private String status;
}
