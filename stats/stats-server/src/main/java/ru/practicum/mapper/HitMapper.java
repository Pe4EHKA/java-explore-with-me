package ru.practicum.mapper;

import ru.practicum.dto.CreateHitDto;
import ru.practicum.model.Hit;

public class HitMapper {
    public static Hit toHit(CreateHitDto createHitDto) {
        return Hit.builder()
                .app(createHitDto.getApp())
                .uri(createHitDto.getUri())
                .ip(createHitDto.getIp())
                .timestamp(createHitDto.getTimestamp())
                .build();
    }

    public static CreateHitDto toCreateHitDto(Hit hit) {
        return CreateHitDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp())
                .build();
    }
}
