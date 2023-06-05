package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatsDto;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@RequestMapping(path = "/stats")
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<StatsDto> get(@RequestParam String start,
                              @RequestParam String end,
                              @RequestParam(required = false) String[] uris,
                              @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Запрос на получение статистики за период {} - {}", start, end);
        List<StatsDto> listToReturn = statsService.get(start, end, uris, unique);
        log.info("Получено элементов: {}", listToReturn.size());
        return listToReturn;
    }
}
