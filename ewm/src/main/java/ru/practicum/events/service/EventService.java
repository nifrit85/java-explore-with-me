package ru.practicum.events.service;

import ru.practicum.enums.EventSort;
import ru.practicum.eventrequests.dto.ParticipationRequestDto;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {

    /**
     * * Получение событий, добавленных текущим пользователем
     *
     * @param id   id текущего пользователя
     * @param from Количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size Количество элементов в наборе
     * @return List<EventShortDto> В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     */

    List<EventShortDto> get(Long id, Integer from, Integer size);

    /**
     * Получение модели события
     *
     * @param id id события
     * @return модель события
     */

    Event get(Long id);

    /**
     * Получение списка моделей Event
     *
     * @param ids список id
     * @return список Event
     */

    Set<Event> getEventIdIn(Set<Long> ids);

    /**
     * Поиск первого события по id категории
     *
     * @param id id категории
     * @return событие
     */

    Event findFirstByCategoryId(Long id);

    /**
     * Добавление нового события
     *
     * @param userId id текущего пользователя
     * @param dto    Данные добавляемого события
     * @return EventFullDto Полное описание события
     */

    EventFullDto create(Long userId, NewEventDto dto);

    /**
     * * Получение полной информации о событии, добавленном текущим пользователем
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return EventFullDto Полное описание события
     */

    EventFullDto get(Long userId, Long eventId);

    /**
     * * Изменение события добавленного текущим пользователем
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @param dto     Новые данные события
     * @return EventFullDto Полное описание события
     */

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest dto);

    /**
     * Получение информации о запросах на участие в событии текущего пользователя
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return List<ParticipationRequestDto> В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     */

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
     *
     * @param userId  id текущего пользователя
     * @param eventId id события текущего пользователя
     * @param dto     Новый статус для заявок на участие в событии текущего пользователя
     * @return Результат подтверждения/отклонения заявок на участие в событии
     */

    EventRequestStatusUpdateResult update(Long userId, Long eventId, EventRequestStatusUpdateRequest dto);

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

    List<EventFullDto> get(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    /**
     * Редактирование данных события и его статуса (отклонение/публикация).
     *
     * @param eventId id события
     * @param dto     Данные для изменения
     * @return EventFullDto Изменённое событие
     */

    EventFullDto update(Long eventId, UpdateEventAdminRequest dto);

    /**
     * Получение событий с возможностью фильтрации
     *
     * @param text          текст для поиска в содержимом аннотации и подробном описании события
     * @param categories    список идентификаторов категорий в которых будет вестись поиск
     * @param paid          поиск только платных/бесплатных событий
     * @param rangeStart    дата и время не раньше которых должно произойти событие
     * @param rangeEnd      дата и время не позже которых должно произойти событие
     * @param onlyAvailable только события у которых не исчерпан лимит запросов на участие
     * @param sort          Вариант сортировки: по дате события или по количеству просмотров: EVENT_DATE, VIEWS
     * @param from          количество событий, которые нужно пропустить для формирования текущего набора
     * @param size          количество событий в наборе
     * @param request       технические данные о http запросе
     * @return Список событий
     */

    List<EventShortDto> get(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                            LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort,
                            Integer from, Integer size, HttpServletRequest request);

    /**
     * Получение подробной информации об опубликованном событии по его идентификатору
     *
     * @param id      id события
     * @param request технические данные о http запросе
     * @return EventFullDto события
     */

    EventFullDto get(Long id, HttpServletRequest request);

}
