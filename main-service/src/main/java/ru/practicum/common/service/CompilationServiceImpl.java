package ru.practicum.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.dto.compilation.CompilationDto;
import ru.practicum.common.dto.compilation.NewCompilationDto;
import ru.practicum.common.dto.compilation.UpdateCompilationRequest;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.common.mapper.CompilationMapper;
import ru.practicum.common.mapper.PropertyMerger;
import ru.practicum.common.model.Compilation;
import ru.practicum.common.model.Event;
import ru.practicum.common.repository.CompilationRepository;
import ru.practicum.common.repository.event.EventRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

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

    @Override
    @Transactional
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        compilation.setEvents(getEventsCompilation(newCompilationDto.getEvents()));

        try {
            compilation = compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }

        log.info("Saved compilation: {}", compilation);

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilationOld = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id: %d not found", compId)));

        Set<Event> eventsOld = compilationOld.getEvents();

        if (updateCompilationRequest.getEvents() != null) {
            eventsOld = getEventsCompilation(updateCompilationRequest.getEvents());
        }

        Compilation updateCompilation = CompilationMapper.toCompilation(updateCompilationRequest);

        updateCompilation.setEvents(eventsOld);

        PropertyMerger.mergeProperties(updateCompilation, compilationOld);

        try {
            compilationOld = compilationRepository.save(compilationOld);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }
        log.info("Updated compilation: {}", compilationOld);

        return CompilationMapper.toCompilationDto(compilationOld);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        log.info("Delete compilation with id {}", compId);
        compilationRepository.deleteById(compId);
        compilationRepository.flush();
    }

    private Set<Event> getEventsCompilation(Set<Long> eventsIds) {
        if (eventsIds == null || eventsIds.isEmpty()) {
            return Set.of();
        }
        return eventRepository.findAllByIdIn(eventsIds);
    }
}
