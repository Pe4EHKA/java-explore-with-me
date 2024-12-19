package ru.practicum.publicApi.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.dto.compilation.CompilationDto;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.common.mapper.CompilationMapper;
import ru.practicum.common.model.Compilation;
import ru.practicum.common.repository.CompilationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));

        List<Compilation> compilations = pinned != null ? compilationRepository.findAllByPinned(pinned, pageRequest) :
                compilationRepository.findAll(pageRequest).toList();

        log.info("Got {} compilations", compilations.size());

        return CompilationMapper.toCompilationDtoList(compilations);
    }

    @Override
    public CompilationDto getCompilation(Long comId) {
        Compilation compilation = compilationRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Compilation with id " + comId + " not found"));

        log.info("Got compilation {}", compilation);

        return CompilationMapper.toCompilationDto(compilation);
    }
}
