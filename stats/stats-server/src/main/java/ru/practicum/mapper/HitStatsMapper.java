package ru.practicum.mapper;

import ru.practicum.dto.HitStatsDto;
import ru.practicum.model.HitStats;

import java.util.List;

public class HitStatsMapper {
    public static HitStats toHitStats(HitStatsDto hitStatsDto) {
        return HitStats.builder()
                .app(hitStatsDto.getApp())
                .uri(hitStatsDto.getUri())
                .hits(hitStatsDto.getHits())
                .build();
    }

    public static HitStatsDto toHitStatsDto(HitStats hitStats) {
        return HitStatsDto.builder()
                .app(hitStats.getApp())
                .uri(hitStats.getUri())
                .hits(hitStats.getHits())
                .build();
    }

    public static List<HitStatsDto> toHitStatsDtoList(List<HitStats> hitStatsList) {
        return hitStatsList.stream()
                .map(HitStatsMapper::toHitStatsDto)
                .toList();
    }
}
