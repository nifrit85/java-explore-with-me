package ru.practicum.location.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.LocationNewDto;
import ru.practicum.location.service.LocationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@RequestMapping(path = "/admin/location")
@Slf4j
@RequiredArgsConstructor
@Validated
public class LocationControllerAdmin {

    private final LocationService locationService;

    /**
     * Создание локации
     *
     * @param dto данные локации
     * @return данные созданной локации
     */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationFullDto create(@Valid @RequestBody LocationNewDto dto) {
        log.info("Запрос на добавление локации {}", dto);
        return locationService.create(dto);
    }

    /**
     * Обновление данных локации
     *
     * @param id  id локации для обновления
     * @param dto данные для обновления
     * @return обновлённые данные локации
     */

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LocationFullDto update(@Valid @Positive @PathVariable Long id,
                                  @RequestBody LocationNewDto dto) {
        log.info("Запрос на изменение локации {}", id);
        return locationService.update(id, dto);
    }

    /**
     * Удаление локации
     *
     * @param id id локации
     */

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@Valid @Positive @PathVariable Long id) {
        log.info("Запрос на удаление локации {}", id);
        locationService.delete(id);
    }

    /**
     * Получение списка локаций
     *
     * @param ids  список id локаций
     * @param from количество локаций, которые нужно пропустить для формирования текущего набора
     * @param size количество локаций в наборе
     * @return Список найденных локаций
     */

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<LocationFullDto> get(@RequestParam(defaultValue = "0") Integer from,
                              @RequestParam(defaultValue = "10") Integer size,
                              @RequestParam List<Long> ids) {
        log.info("Запрос на получение списка локации");
        return locationService.get(ids, from, size);
    }
}
