package ru.practicum.publicApi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.compilation.CompilationDto;
import ru.practicum.publicApi.service.compilation.PublicCompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationsController {

    private final PublicCompilationService publicCompilationService;

    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        log.info("PUBLIC GET all compilations with from {}, size {}", from, size);
        return publicCompilationService.getAllCompilations(pinned, from, size);
    }

    @GetMapping("/{comId}")
    public CompilationDto getCompilation(@PathVariable("comId") Long comId) {
        log.info("PUBLIC GET compilation with id {}", comId);
        return publicCompilationService.getCompilation(comId);
    }
}
