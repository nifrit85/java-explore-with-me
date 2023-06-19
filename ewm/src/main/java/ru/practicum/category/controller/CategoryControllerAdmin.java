package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import static ru.practicum.category.mapper.CategoryMapper.toModel;

@RestController
@RequestMapping(path = "/admin/categories")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CategoryControllerAdmin {
    private final CategoryService categoryService;

    /**
     * Добавление новой категории
     *
     * @param dto данные добавляемой категории
     * @return данные добавленной категории
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody NewCategoryDto dto) {
        log.info("Запрос на добавление категории {}", dto);
        return categoryService.create(toModel(dto));
    }

    /**
     * Изменение категории
     *
     * @param catId id категории
     * @param dto   Данные категории для изменения
     * @return Данные изменённой категории
     */

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@Valid @Positive @PathVariable Long catId,
                              @Valid @RequestBody CategoryDto dto) {
        log.info("Запрос на изменение категории id = {}, данные для изменения {}", catId, dto);
        return categoryService.update(catId, dto);
    }

    /**
     * Удаление категории
     *
     * @param catId id категории
     */

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Valid @Positive @PathVariable Long catId) {
        log.info("Запрос на удаление категории {}", catId);
        categoryService.delete(catId);
    }
}
