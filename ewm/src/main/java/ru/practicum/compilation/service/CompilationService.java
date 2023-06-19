package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    /**
     * Получение подборок событий
     *
     * @param pinned искать только закрепленные/не закрепленные подборки
     * @param from   количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size   количество элементов в наборе
     * @return Список подборок событий
     */

    List<CompilationDto> get(Boolean pinned, Integer from, Integer size);

    /**
     * Получение подборки событий по её id
     *
     * @param id id подборки
     * @return Подборка событий
     */
    CompilationDto get(Long id);

    /**
     * Добавление новой подборки (подборка может не содержать событий)
     *
     * @param dto данные новой подборки
     * @return Подборка событий
     */

    CompilationDto create(NewCompilationDto dto);

    /**
     * Удаление подборки
     *
     * @param id id подборки
     */

    void delete(Long id);

    /**
     * Обновление информации о подборке
     *
     * @param id  id подборки
     * @param dto Данные для обновления подборки
     * @return Подборка событий
     */

    CompilationDto update(Long id, UpdateCompilationRequest dto);
}
