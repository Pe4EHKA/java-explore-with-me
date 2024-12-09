package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HitStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
