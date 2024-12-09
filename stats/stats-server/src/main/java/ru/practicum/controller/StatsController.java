package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CreateHitDto;
import ru.practicum.dto.HitStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<CreateHitDto> createHit(@RequestBody CreateHitDto createHitDto) {
        log.info("Create hit request: {}", createHitDto);
        return new ResponseEntity<>(statsService.save(createHitDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<HitStatsDto>> getStats(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                      @RequestParam(name = "start") LocalDateTime start,
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                      @RequestParam(name = "end") LocalDateTime end,
                                                      @RequestParam(name = "uris", required = false) List<String> uris,
                                                      @RequestParam(name = "unique", defaultValue = "false")
                                                      Boolean unique) {
        log.info("Get stats request");
        return new ResponseEntity<>(statsService.getStats(start, end, uris, unique), HttpStatus.OK);
    }

}
