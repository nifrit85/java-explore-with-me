package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/admin/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CompilationControllerAdmin {
    private final CompilationService compilationService;

    /**
     * Добавление новой подборки (подборка может не содержать событий)
     *
     * @param dto данные новой подборки
     * @return Подборка событий
     */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody NewCompilationDto dto) {
        log.info("Добавление новой подборки: {}", dto);
        return compilationService.create(dto);
    }

    /**
     * Удаление подборки
     *
     * @param compId id подборки
     */
    @DeleteMapping(value = "/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Valid @Positive @PathVariable Long compId) {
        log.info("Удаление подборки: id = {}", compId);
        compilationService.delete(compId);
    }

    /**
     * Обновление информации о подборке
     *
     * @param compId id подборки
     * @param dto    Данные для обновления подборки
     * @return Подборка событий
     */
    @PatchMapping("/{compId}")
    public CompilationDto update(@Valid @Positive @PathVariable Long compId,
                                 @Valid @RequestBody UpdateCompilationRequest dto) {
        log.info("Обновление информации о подборке: id = {}, данные для обновления {}", compId, dto);
        return compilationService.update(compId, dto);
    }
}
