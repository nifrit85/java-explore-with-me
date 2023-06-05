package ru.practicum.eventrequests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventrequests.dto.ParticipationRequestDto;
import ru.practicum.eventrequests.service.EventRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@Slf4j
@RequiredArgsConstructor
public class EventRequestControllerPrivate {
    private final EventRequestService eventRequestService;

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     *
     * @param userId id текущего пользователя
     * @return List<ParticipationRequestDto> В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     */

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> get(@Valid @Positive @PathVariable Long userId) {
        log.info("Получение информации о заявках текущего пользователя c id = {} на участие в чужих событиях", userId);
        return eventRequestService.get(userId);
    }

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return Заявка на участие в событии
     */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@Valid @Positive @PathVariable Long userId,
                                          @Valid @Positive @RequestParam Long eventId) {
        log.info("Создание запроса на участие в событии с id {} пользователем с id {}", eventId, userId);
        return eventRequestService.create(userId, eventId);
    }

    /**
     * Отмена своего запроса на участие в событии
     *
     * @param userId    id текущего пользователя
     * @param requestId id запроса на участие
     * @return Заявка на участие в событии
     */

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancel(@Valid @Positive @PathVariable Long userId,
                                          @Valid @Positive @PathVariable Long requestId) {
        log.info("Отмена своего запроса на участие в событии. Id пользователя {}, id запроса {}", userId, requestId);
        return eventRequestService.cancel(userId, requestId);
    }
}
