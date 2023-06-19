package ru.practicum.location.service;

import ru.practicum.location.model.Location;

public interface LocationService {
    /**
     * Получение локации по координатам
     *
     * @param lat широта
     * @param lon долгота
     * @return локация
     */

    Location getByLatAndLon(Float lat, Float lon);

    /**
     * Сохранение локации
     *
     * @param location локация
     * @return локация
     */

    Location create(Location location);

    /**
     * Проверка на существоание локации по координатам
     *
     * @param lat широта
     * @param lon долгота
     * @return истина, если существует
     */

    boolean existByLatAndLon(Float lat, Float lon);

    /**
     * Если локация не существует, она будет создана
     *
     * @param lat широта
     * @param lon долгота
     * @return локация
     */

    Location getOrSave(Float lat, Float lon);
}
