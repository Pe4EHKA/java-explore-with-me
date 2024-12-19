package ru.practicum.adminApi.service.compilation;

import ru.practicum.common.dto.compilation.CompilationDto;
import ru.practicum.common.dto.compilation.NewCompilationDto;
import ru.practicum.common.dto.compilation.UpdateCompilationRequest;

public interface AdminCompilationService {

    CompilationDto save(NewCompilationDto newCompilationDto);

    CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest);

    void delete(Long compId);
}
