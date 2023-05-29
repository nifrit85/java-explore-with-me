package ru.practicum.service.hit;

import ru.practicum.hit.dto.HitDto;

public interface HitService {

    /**
     * Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.
     * Название сервиса, uri и ip пользователя указаны в теле запроса.
     *
     * @param dto данные запроса
     */

    void create(HitDto dto);
}
