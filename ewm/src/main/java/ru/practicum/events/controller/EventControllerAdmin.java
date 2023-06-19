package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.UpdateEventAdminRequest;
import ru.practicum.events.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventControllerAdmin {

    private final EventService eventService;

    /**
     * Поиск событий
     *
     * @param users      список id пользователей, чьи события нужно найти
     * @param states     список состояний в которых находятся искомые события
     * @param categories список id категорий в которых будет вестись поиск
     * @param rangeStart дата и время не раньше которых должно произойти событие
     * @param rangeEnd   дата и время не позже которых должно произойти событие
     * @param from       количество событий, которые нужно пропустить для формирования текущего набора
     * @param size       количество событий в наборе
     * @return List<EventFullDto> В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     */

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> get(@RequestParam(required = false) List<Long> users,
                                  @RequestParam(required = false) List<String> states,
                                  @RequestParam(required = false) List<Long> categories,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поиск событий по параметрам: users = {}, states = {}, categories = {}, rangeStart = {}, rangeEnd = {}", users, states, categories, rangeStart, rangeEnd);
        return eventService.get(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    /**
     * Редактирование данных события и его статуса (отклонение/публикация).
     *
     * @param eventId id события
     * @param dto     Данные для изменения
     * @return EventFullDto Изменённое событие
     */

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@Valid @Positive @PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventAdminRequest dto) {
        log.info("Обновление информации о событии с id = {}, данные для обновления {}", eventId, dto);
        return eventService.update(eventId, dto);
    }

}
