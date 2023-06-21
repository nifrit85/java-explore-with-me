package ru.practicum.eventrequests.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.enums.Status;
import ru.practicum.eventrequests.dto.ParticipationRequestDto;
import ru.practicum.eventrequests.mapper.EventRequestMapper;
import ru.practicum.eventrequests.model.EventRequest;
import ru.practicum.eventrequests.repository.EventRequestRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.users.model.User;
import ru.practicum.users.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.enums.State.PUBLISHED;
import static ru.practicum.enums.Status.*;
import static ru.practicum.eventrequests.mapper.EventRequestMapper.toParticipation;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Lazy})
public class EventRequestServiceImpl implements EventRequestService {

    private static final String REQUEST = "Request";
    private final EventRequestRepository eventRequestRepository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> get(Long id) {
        log.debug("Получение информации о заявках текущего пользователя c id = {} на участие в чужих событиях", id);
        userService.exists(id);
        return eventRequestRepository.findAllByRequesterId(id).stream()
                .map(EventRequestMapper::toParticipation)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {
        log.debug("Создание запроса на участие в событии с id {} пользователем с id {}", eventId, userId);
        User user = userService.get(userId);
        Event event = eventService.get(eventId);

        checkBeforeCreate(event, userId);

        EventRequest eventRequest = EventRequest.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            eventRequest.setStatus(CONFIRMED);
        } else {
            eventRequest.setStatus(PENDING);
        }

        return toParticipation(eventRequestRepository.save(eventRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        log.debug("Отмена своего запроса на участие в событии. Id пользователя {}, id запроса {}", userId, requestId);

        userService.exists(userId);

        EventRequest eventRequest = eventRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(REQUEST, requestId));

        if (!eventRequest.getRequester().getId().equals(userId)) {
            throw new NotFoundException(REQUEST, requestId);
        }

        eventRequest.setStatus(CANCELED);

        return toParticipation(eventRequestRepository.save(eventRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventRequest> findAllByEventInAndStatus(List<Event> events, Status status) {
        log.debug("Получение списка запросов по статусу. Список :{}, статус {}", events, status);
        return eventRequestRepository.findAllByEventInAndStatus(events, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventRequest> findByEventIn(List<Event> events) {
        log.debug("Получение списка запросов. Список :{}", events);
        return eventRequestRepository.findByEventIn(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventRequest> findAllById(List<Long> ids) {
        log.debug("Получение списка запросов. Список :{}", ids);
        return eventRequestRepository.findAllByIdIn(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByEventIdAndStatus(Long eventId, Status status) {
        log.debug("Получение количества подтверждённых запросов. {} {}", eventId, status);
        return eventRequestRepository.countByEventIdAndStatus(eventId, status);
    }

    @Override
    @Transactional
    public List<EventRequest> saveAll(List<EventRequest> requests) {
        log.debug("Сохраняем запросы :{}", requests);
        return eventRequestRepository.saveAll(requests);
    }

    /**
     * Проверки перед сохранением запроса
     *
     * @param event  событие
     * @param userId id пользователя
     */

    private void checkBeforeCreate(Event event, Long userId) {
        log.debug("Проверки перед сохранением запрос");
        if (eventRequestRepository.findByRequesterIdAndEventId(userId, event.getId()) != null) {
            log.info("Нельзя добавить повторный запрос ");
            throw new ConflictException("You can't add a repeat request.");
        }
        if (event.getInitiator().getId().equals(userId)) {
            log.info("Инициатор события не может добавить запрос на участие в своём событии. Инициатор = {}, Пользователь = {}", event.getInitiator().getId(), userId);
            throw new ConflictException("The initiator of the event cannot add a request to participate in his event");
        }
        if (event.getState() != PUBLISHED) {
            log.info("Нельзя участвовать в неопубликованном событии. Статус = {}", event.getState());
            throw new ConflictException("You can't participate in an unpublished event");
        }
        Long confirmedCount = eventRequestRepository.countByEventIdAndStatus(event.getId(), CONFIRMED);
        if (event.getParticipantLimit() != 0 && confirmedCount >= event.getParticipantLimit()) {
            log.info("У события достигнут лимит запросов на участие. Лимит = {}, Подтверждённое количество = {}", event.getParticipantLimit(), confirmedCount);
            throw new ConflictException("Reach participant limit.");
        }
    }
}
