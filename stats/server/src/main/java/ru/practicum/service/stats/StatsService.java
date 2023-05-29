package ru.practicum.service.stats;

import ru.practicum.stats.dto.StatsDto;

import java.util.List;

public interface StatsService {

    /**
     * Получение статистики по посещениям.
     *
     * @param start  Дата и время начала диапазона за который нужно выгрузить статистику (в формате "yyyy-MM-dd HH:mm:ss")
     * @param end    Дата и время конца диапазона за который нужно выгрузить статистику (в формате "yyyy-MM-dd HH:mm:ss")
     * @param uris   Список uri для которых нужно выгрузить статистику
     * @param unique Нужно ли учитывать только уникальные посещения (только с уникальным ip)
     * @return Список StatsDto
     */

    List<StatsDto> get(String start, String end, String[] uris, boolean unique);
}
