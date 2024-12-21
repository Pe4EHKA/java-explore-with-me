package ru.practicum.common.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.common.dto.compilation.CompilationDto;
import ru.practicum.common.dto.compilation.NewCompilationDto;
import ru.practicum.common.dto.compilation.UpdateCompilationRequest;
import ru.practicum.common.model.Compilation;

import java.util.List;
import java.util.Set;

@UtilityClass
public class CompilationMapper {

    public List<CompilationDto> toCompilationDtoList(List<Compilation> compilations) {
        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .toList();
    }

    public Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .pinned(newCompilationDto.isPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public Compilation toCompilation(UpdateCompilationRequest updateCompilationRequest) {
        return Compilation.builder()
                .title(updateCompilationRequest.getTitle())
                .pinned(updateCompilationRequest.getPinned())
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents() != null ? EventMapper
                        .toEventShortDtoSet(compilation.getEvents()) : Set.of())
                .build();
    }
}
