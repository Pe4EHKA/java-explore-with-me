package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CreateHitDto;
import ru.practicum.dto.HitStatsDto;
import ru.practicum.exception.DateTimeException;
import ru.practicum.mapper.HitMapper;
import ru.practicum.mapper.HitStatsMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public CreateHitDto save(CreateHitDto createHitDto) {
        return HitMapper.toCreateHitDto(statsRepository.save(HitMapper.toHit(createHitDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HitStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new DateTimeException("Time 'start' can't be after 'end'");
        }
        List<HitStatsDto> hitStatsDtos = HitStatsMapper.toHitStatsDtoList(unique ?
                statsRepository.findUniqueHitStats(start, end, uris) :
                statsRepository.findHitStats(start, end, uris));

        log.info("Got stats: {}", hitStatsDtos.toString());

        return hitStatsDtos;
    }

}
