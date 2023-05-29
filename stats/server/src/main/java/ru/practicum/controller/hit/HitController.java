package ru.practicum.controller.hit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.hit.dto.HitDto;
import ru.practicum.service.hit.HitService;

@RestController
@RequestMapping(path = "/hit")
@RequiredArgsConstructor
@Slf4j
public class HitController {

    private final HitService hitService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void create(@RequestBody HitDto dto) {
        log.info("Запрос POST: create(HitDto dto) на добавлении записи в статистику");
        hitService.create(dto);
    }
}
