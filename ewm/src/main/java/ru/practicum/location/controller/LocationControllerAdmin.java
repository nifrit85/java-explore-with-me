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

import static ru.practicum.location.mapper.LocationMapper.ToFullDto;


@RestController
@RequestMapping(path = "/admin/location")
@Slf4j
@RequiredArgsConstructor
@Validated
public class LocationControllerAdmin {

    private final LocationService locationService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationFullDto create(@Valid @RequestBody LocationNewDto dto) {
        log.info("Запрос на добавление локации {}", dto);
        return locationService.create(dto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LocationFullDto update(@Valid @Positive @PathVariable Long id,
                                  @RequestBody LocationNewDto dto) {
        log.info("Запрос на изменение локации {}", id);
        return locationService.update(id, dto);
    }

    @DeleteMapping("/id")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@Valid @Positive @PathVariable Long id) {
        log.info("Запрос на удаление локации {}", id);
        locationService.delete(id);
    }
}
