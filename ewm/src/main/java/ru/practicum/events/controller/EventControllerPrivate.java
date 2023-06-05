package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventrequests.dto.ParticipationRequestDto;
import ru.practicum.events.dto.*;
import ru.practicum.events.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventControllerPrivate {

    private final EventService eventService;

    /**
     * * Получение событий, добавленных текущим пользователем
     *
     * @param userId id текущего пользователя
     * @param from   Количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size   Количество элементов в наборе
     * @return List<EventShortDto> В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     */

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> get(@PathVariable("userId") Long userId,
                                   @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                   @Valid @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на получение событий, добавленных текущим пользователем c id = {}", userId);
        return eventService.get(userId, from, size);
    }

    /**
     * Добавление нового события
     *
     * @param userId id текущего пользователя
     * @param dto    Данные добавляемого события
     * @return EventFullDto Полное описание события
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@Valid @Positive @PathVariable Long userId,
                               @Valid @RequestBody NewEventDto dto) {
        log.info("Добавление нового события: userId = {} , event = {}", userId, dto);
        return eventService.create(userId, dto);
    }

    /**
     * * Получение полной информации о событии, добавленном текущим пользователем
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return EventFullDto Полное описание события
     */
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto get(@Valid @Positive @PathVariable Long userId,
                            @Valid @Positive @PathVariable Long eventId) {
        log.info("Получение полной информации о событии с id = {}, добавленном пользователем c id = {}", eventId, userId);
        return eventService.get(userId, eventId);
    }

    /**
     * * Изменение события добавленного текущим пользователем
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @param dto     Новые данные события
     * @return EventFullDto Полное описание события
     */
    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@Valid @Positive @PathVariable Long userId,
                               @Valid @Positive @PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventUserRequest dto) {
        log.info("Обновление информации о событии с id = {}, пользователем с id = {}, данные {}", eventId, userId, dto);
        return eventService.update(userId, eventId, dto);
    }

    /**
     * Получение информации о запросах на участие в событии текущего пользователя
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return List<ParticipationRequestDto> В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     */
    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@Valid @Positive @PathVariable Long userId,
                                                     @Valid @Positive @PathVariable Long eventId) {
        log.info("Получение информации о запросах на участие в событии с id = {} пользователя с id = {}", eventId, userId);
        return eventService.getRequests(userId, eventId);
    }

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
     *
     * @param userId  id текущего пользователя
     * @param eventId id события текущего пользователя
     * @param dto     Новый статус для заявок на участие в событии текущего пользователя
     * @return Результат подтверждения/отклонения заявок на участие в событии
     */
    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestsStatus(@Valid @Positive @PathVariable Long userId,
                                                               @Valid @Positive @PathVariable Long eventId,
                                                               @Valid @RequestBody EventRequestStatusUpdateRequest dto) {
        log.info("Изменение статуса на {}  заявок на участие в событии с id = {} пользователя с id = {}", dto.getStatus(), eventId, userId);
        return eventService.update(userId, eventId, dto);
    }
}
