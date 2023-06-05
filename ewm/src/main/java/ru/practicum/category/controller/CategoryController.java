package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/categories")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * Получение категорий
     *
     * @param from количество категорий, которые нужно пропустить для формирования текущего набора (по умолчанию 0)
     * @param size количество категорий в наборе (по умолчанию 10)
     * @return Список категорий или пустой список
     */

    @GetMapping
    @ResponseStatus(OK)
    public List<CategoryDto> get(@Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                 @Valid @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на получение всех категорий");
        return categoryService.get(from, size);
    }

    /**
     * Получение информации о категории по её идентификатору
     *
     * @param catId id категории
     * @return В случае, если категории с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto get(@Valid @Positive @PathVariable Long catId) {
        log.info("Запрос на получение категории {}", catId);
        return categoryService.get(catId);
    }
}
