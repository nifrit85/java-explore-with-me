package ru.practicum.location.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.service.LocationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.location.mapper.LocationMapper.toFullDto;

@RestController
@RequestMapping(path = "/location")
@Slf4j
@RequiredArgsConstructor
@Validated
public class LocationController {

    private final LocationService locationService;

    /**
     * Получение локации по id
     *
     * @param id id искомой локации
     * @return искомая локация
     */

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    LocationFullDto get(@Valid @Positive @PathVariable Long id) {
        log.info("Запрос на получение локации с id = {}", id);
        return toFullDto(locationService.get(id));
    }

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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<LocationFullDto> get(@Valid @RequestParam Float lat,
                              @Valid @RequestParam Float lon,
                              @Valid @Positive @RequestParam Float radius,
                              @RequestParam(defaultValue = "0") Integer from,
                              @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на получение списка локаций в радиусе {} от координат {} : {}", radius, lat, lon);
        return locationService.get(lat, lon, radius, from, size).stream()
                .map(LocationMapper::toFullDto)
                .collect(Collectors.toList());
    }

}
