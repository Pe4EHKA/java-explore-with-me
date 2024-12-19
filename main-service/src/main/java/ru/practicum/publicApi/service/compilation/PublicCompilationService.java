package ru.practicum.publicApi.service.compilation;

import ru.practicum.common.dto.compilation.CompilationDto;

import java.util.List;

public interface PublicCompilationService {

    List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilation(Long comId);
}
