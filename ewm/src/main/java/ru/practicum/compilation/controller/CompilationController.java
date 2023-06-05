package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CompilationController {
    private final CompilationService compilationService;

    /**
     * Получение подборок событий
     *
     * @param pinned искать только закрепленные/не закрепленные подборки
     * @param from   количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size   количество элементов в наборе
     * @return Список подборок событий
     */

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> get(
            @RequestParam(required = false) Boolean pinned,
            @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Valid @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение подборок событий по параметрам: pinned = {}, from = {}, size = {}", pinned, from, size);
        return compilationService.get(pinned, from, size);
    }

    /**
     * Получение подборки событий по её id
     *
     * @param compId id подборки
     * @return Подборка событий
     */

    @GetMapping("/{compId}")
    public CompilationDto get(@Valid @Positive @PathVariable Long compId) {
        log.info("Получение подборки событий по её id = {}", compId);
        return compilationService.get(compId);
    }
}
