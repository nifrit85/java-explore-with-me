package ru.practicum;

import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatClient {

    /**
     * * Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.
     *
     * @param app Название сервиса
     * @param uri uri конкретного сервиса
     * @param ip  ip пользователя
     */
    void save(String app, String uri, String ip);


    /**
     * Получение статистики по посещениям.
     *
     * @param start  Дата и время начала диапазона за который нужно выгрузить статистику
     * @param end    Дата и время конца диапазона за который нужно выгрузить статистику
     * @param uris   Список uri для которых нужно выгрузить статистику
     * @param unique Нужно ли учитывать только уникальные посещения (только с уникальным ip)
     * @return Список StatsDto
     */
    List<StatsDto> get(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique);
}
