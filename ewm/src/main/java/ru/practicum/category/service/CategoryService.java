package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryService {

    /**
     * Добавление новой категории
     *
     * @param category данные добавляемой категории
     * @return данные добавленной категории
     */

    CategoryDto create(Category category);

    /**
     * Получение категорий
     *
     * @param from количество категорий, которые нужно пропустить для формирования текущего набора (по умолчанию 0)
     * @param size количество категорий в наборе (по умолчанию 10)
     * @return Список категорий или пустой список
     */

    List<CategoryDto> get(Integer from, Integer size);

    /**
     * Получение информации о категории по её идентификатору
     *
     * @param id id категории
     * @return В случае, если категории с заданным id не найдено, возвращает статус код 404
     */
    CategoryDto get(Long id);

    /**
     * Изменение категории
     *
     * @param id  id категории
     * @param dto Данные категории для изменения
     * @return Данные изменённой категории
     */

    CategoryDto update(Long id, CategoryDto dto);

    /**
     * Проверка на существование категории
     *
     * @param id id категории
     */

    void existsById(Long id);

    /**
     * Удаление категории
     *
     * @param id id категории
     */

    void delete(Long id);

    /**
     * Получение модели категории
     *
     * @param id id категории
     * @return Модель категории
     */

    Category getCategory(Long id);
}
