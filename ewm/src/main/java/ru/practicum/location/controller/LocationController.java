package ru.practicum.location.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.location.service.LocationService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/location")
@Slf4j
@RequiredArgsConstructor
@Validated
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<EventFullDto> get(@Valid @NotNull @PathVariable Float lat,
                           @Valid @NotNull @PathVariable Float lon,
                           @Valid @NotNull @PathVariable Float radius) {
        log.info("Запрос на получение списка событий в радиусе {} от координат {} : {}", radius, lat, lon);
    }

}
