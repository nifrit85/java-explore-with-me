package ru.practicum.eventrequests.service;

import ru.practicum.enums.Status;
import ru.practicum.eventrequests.dto.ParticipationRequestDto;
import ru.practicum.eventrequests.model.EventRequest;
import ru.practicum.events.model.Event;

import java.util.List;

public interface EventRequestService {

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     *
     * @param id id текущего пользователя
     * @return List<ParticipationRequestDto> В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     */
    List<ParticipationRequestDto> get(Long id);

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return Заявка на участие в событии
     */

    ParticipationRequestDto create(Long userId, Long eventId);

    /**
     * Отмена своего запроса на участие в событии
     *
     * @param userId    id текущего пользователя
     * @param requestId id запроса на участие
     * @return Заявка на участие в событии
     */

    ParticipationRequestDto cancel(Long userId, Long requestId);

    /**
     * Получение списка запросов к списку событий с определённым статусом
     *
     * @param events список событий
     * @param status статус
     * @return список запросов
     */

    List<EventRequest> findAllByEventInAndStatus(List<Event> events, Status status);

    /**
     * Получение списка запросов к списку событий
     *
     * @param events список событий
     * @return список запросов
     */

    List<EventRequest> findByEventIn(List<Event> events);

    /**
     * Получение списка запросов
     *
     * @param ids список id запросов
     * @return список запросов
     */

    List<EventRequest> findAllById(List<Long> ids);

    /**
     * Получение количества запросов к событию с определённым статусом
     *
     * @param eventId id события
     * @param status  статус
     * @return количество запросов
     */

    Long countByEventIdAndStatus(Long eventId, Status status);

    /**
     * Сохранение запросов массово
     *
     * @param requests список запросов
     * @return список запросов
     */

    List<EventRequest> saveAll(List<EventRequest> requests);
}
