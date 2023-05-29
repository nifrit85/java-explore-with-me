package ru.practicum.controller.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.stats.StatsService;
import ru.practicum.stats.dto.StatsDto;

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
        log.info("Запрос GET: get(String start, String end, String[] uris, boolean unique) на получение статистики");
        return statsService.get(start, end, uris, unique);
    }
}
