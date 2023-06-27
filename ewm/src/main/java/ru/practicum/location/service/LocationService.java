package ru.practicum.location.service;

import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.LocationNewDto;
import ru.practicum.location.model.Location;

import java.util.List;

public interface LocationService {
    /**
     * Получение локации по координатам
     *
     * @param lat широта
     * @param lon долгота
     * @return локация
     */

    Location get(Float lat, Float lon);

    /**
     * Сохранение локации
     *
     * @param location локация
     * @return локация
     */

    Location create(Location location);

    /**
     * Проверка на существование локации по координатам
     *
     * @param lat широта
     * @param lon долгота
     * @return истина, если существует
     */

    boolean exist(Float lat, Float lon);

    /**
     * Если локация не существует, она будет создана
     *
     * @param lat широта
     * @param lon долгота
     * @return локация
     */

    Location getOrSave(Float lat, Float lon);

    /**
     * Обновление данных локации
     *
     * @param id  id локации для обновления
     * @param dto данные для обновления
     * @return обновлённые данные локации
     */
    LocationFullDto update(Long id, LocationNewDto dto);

    /**
     * Проверка локации на существование
     *
     * @param id id искомой локации
     */

    void exist(Long id);

    /**
     * Получение локации по id
     *
     * @param id id искомой локации
     * @return искомая локация
     */
    Location get(Long id);

    /**
     * Создание локации
     *
     * @param dto данные локации
     * @return данные созданной локации
     */
    LocationFullDto create(LocationNewDto dto);

    /**
     * Удаление локации
     *
     * @param id id локации
     */
    void delete(Long id);

    /**
     * Получение списка локаций
     *
     * @param ids  список id локаций
     * @param from количество локаций, которые нужно пропустить для формирования текущего набора
     * @param size количество локаций в наборе
     * @return Список найденных локаций
     */
    List<LocationFullDto> get(List<Long> ids, Integer from, Integer size);

    /**
     * Получение списка локаций на расстоянии от координат
     *
     * @param lat    текущая широта
     * @param lon    текущая долгота
     * @param radius расстояние для поиска
     * @param from   количество локаций, которые нужно пропустить для формирования текущего набора
     * @param size   количество локаций в наборе
     * @return Список событий
     */
    List<Location> get(Float lat, Float lon, Float radius, Integer from, Integer size);
}
