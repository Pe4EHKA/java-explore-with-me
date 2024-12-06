package ru.practicum.service;

import ru.practicum.dto.CreateHitDto;
import ru.practicum.dto.HitStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    public CreateHitDto save(CreateHitDto createHitDto);

    public List<HitStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}