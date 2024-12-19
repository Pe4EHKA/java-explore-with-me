package ru.practicum.adminApi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.adminApi.service.compilation.AdminCompilationService;
import ru.practicum.common.dto.compilation.CompilationDto;
import ru.practicum.common.dto.compilation.NewCompilationDto;
import ru.practicum.common.dto.compilation.UpdateCompilationRequest;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationController {
    private final AdminCompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto save(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("POST Admin New compilation: {}", newCompilationDto);
        return compilationService.save(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto update(@Valid @RequestBody UpdateCompilationRequest updateCompilationRequest,
                                 @PathVariable("compId") Long compId) {
        log.info("PATCH Admin Update id: {}, compilation: {}", compId, updateCompilationRequest);
        return compilationService.update(compId, updateCompilationRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("compId") Long compId) {
        log.info("DELETE Admin Delete id: {}", compId);
        compilationService.delete(compId);
    }
}
