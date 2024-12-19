package ru.practicum.adminApi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestParamForEvent {

    private List<Long> users;

    private List<Long> categories;

    private LocalDateTime rangeStart;

    private LocalDateTime rangeEnd;

    private List<String> states;

    private int from;

    private int size;
}
