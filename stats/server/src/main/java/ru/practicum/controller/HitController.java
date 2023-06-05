package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.service.HitService;

@RestController
@RequestMapping(path = "/hit")
@RequiredArgsConstructor
@Slf4j
public class HitController {

    private final HitService hitService;

    /**
     * Добавление записи в статистику
     *
     * @param dto данные для сохранения
     */

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void create(@RequestBody HitDto dto) {
        log.info("Запрос app = {}, uri = {}, ip ={} на добавлении записи в статистику", dto.getApp(), dto.getUri(), dto.getIp());
        hitService.create(dto);
    }
}
