package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.events.service.EventService;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.compilation.mapper.CompilationMapper.toDto;
import static ru.practicum.compilation.mapper.CompilationMapper.toModel;


@Service
@AllArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private static final String COMPILATION = "Compilation";
    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> get(Boolean pinned, Integer from, Integer size) {
        log.debug("Получение подборок событий по параметрам: pinned = {}, from = {}, size = {}", pinned, from, size);
        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size);

        if (pinned == null) {
            return compilationRepository.findAll(pageable).stream().map(CompilationMapper::toDto).collect(Collectors.toList());
        } else {
            return compilationRepository.findByPinned(pinned, pageable).stream().map(CompilationMapper::toDto).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto get(Long id) {
        log.debug("Получение подборки событий по её id = {}", id);
        return toDto(compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(COMPILATION, id)));
    }

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto dto) {
        log.debug("Добавление новой подборки: {}", dto);
        Compilation compilation = toModel(dto);
        if (dto.getEvents() != null) {
            compilation.setEvents(eventService.getEventIdIn(dto.getEvents()));
        }
        return toDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Удаление подборки: id = {}", id);
        existsById(id);
        compilationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CompilationDto update(Long id, UpdateCompilationRequest dto) {
        log.debug("Обновление информации о подборке: id = {}, данные для обновления {}", id, dto);
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() -> new NotFoundException(COMPILATION, id));
        if (dto.getEvents() != null) {
            compilation.setEvents(eventService.getEventIdIn(dto.getEvents()));
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        return toDto(compilationRepository.save(compilation));
    }

    /**
     * Проверка на существование подборки
     *
     * @param id id подборки
     */

    private void existsById(Long id) {
        log.debug("Запрос на существование подборки c id = {}", id);
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException(COMPILATION, id);
        }
    }
}
