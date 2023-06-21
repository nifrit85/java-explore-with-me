package ru.practicum.events.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatClient;
import ru.practicum.category.service.CategoryService;
import ru.practicum.dto.StatsDto;
import ru.practicum.enums.EventSort;
import ru.practicum.enums.State;
import ru.practicum.enums.Status;
import ru.practicum.eventrequests.dto.ParticipationRequestDto;
import ru.practicum.eventrequests.mapper.EventRequestMapper;
import ru.practicum.eventrequests.model.EventRequest;
import ru.practicum.eventrequests.service.EventRequestService;
import ru.practicum.events.dto.*;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationRequestException;
import ru.practicum.location.model.Location;
import ru.practicum.location.service.LocationService;
import ru.practicum.users.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.enums.State.*;
import static ru.practicum.enums.StateActionAdmin.PUBLISH_EVENT;
import static ru.practicum.enums.StateActionAdmin.REJECT_EVENT;
import static ru.practicum.enums.StateActionUser.CANCEL_REVIEW;
import static ru.practicum.enums.StateActionUser.SEND_TO_REVIEW;
import static ru.practicum.enums.Status.CONFIRMED;
import static ru.practicum.enums.Status.REJECTED;
import static ru.practicum.events.mapper.EventMapper.toFullDto;
import static ru.practicum.events.mapper.EventMapper.toModel;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Lazy})
public class EventServiceImpl implements EventService {
    private final UserService userService;
    private final EventRepository eventRepository;
    private final StatClient statsClient;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final EventRequestService eventRequestService;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> get(Long id, Integer from, Integer size) {
        log.debug("Запрос на получение событий, добавленных текущим пользователем c id = {}", id);
        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size);

        userService.exists(id);
        List<Event> events = eventRepository.findAllByInitiatorId(id, pageable);

        return convertToShortDtoList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public Event get(Long id) {
        log.debug("Поучением событие {}", id);
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Event> getEventIdIn(Set<Long> ids) {
        log.debug("Поучением список событий  {}", ids);
        return eventRepository.findByIdIn(ids);
    }

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto dto) {
        log.debug("Добавление нового события: userId = {} , event = {}", userId, dto);
        Event event = toModel(dto);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationRequestException("Cannot create the event, because less 2 hours before event datetime");
        }
        event.setCategory(categoryService.getCategory(dto.getCategory()));
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(userService.get(userId));
        event.setState(PENDING);
        event.setLocation(locationService.getOrSave(dto.getLocation().getLat(), dto.getLocation().getLon()));
        EventFullDto eventFullDto = toFullDto(eventRepository.save(event));
        eventFullDto.setViews(0L);
        eventFullDto.setConfirmedRequests(0L);
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Event findFirstByCategoryId(Long id) {
        log.debug("Проверяем что в категории {}, есть события", id);
        return eventRepository.findFirstByCategoryId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto get(Long userId, Long eventId) {
        log.debug("Получение полной информации о событии с id = {}, добавленном пользователем c id = {}", eventId, userId);
        userService.exists(userId);
        Event event = get(eventId);
        EventFullDto eventFullDto = toFullDto(event);
        return fillViewAndConfirmedRequests(eventFullDto, event);
    }

    @Override
    @Transactional
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest dto) {
        log.debug("Обновление информации о событии с id = {}, пользователем с id = {}, данные {}", eventId, userId, dto);
        userService.exists(userId);
        Event event = get(eventId);
        if (event.getState() != null && event.getState() != PENDING && event.getState() != CANCELED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        if (dto.getEventDate() != null
                && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationRequestException("Event date must not be before 2 hours from current time.");
        }

        updateEventFromUserRequest(event, dto);

        EventFullDto eventFullDto = toFullDto(eventRepository.save(event));
        return fillViewAndConfirmedRequests(eventFullDto, event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        log.debug("Получение информации о запросах на участие в событии с id = {} пользователя с id = {}", eventId, userId);
        userService.exists(userId);
        List<Event> events = eventRepository.findByIdAndInitiatorId(eventId, userId);
        return eventRequestService.findByEventIn(events).stream()
                .map(EventRequestMapper::toParticipation)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult update(Long userId, Long eventId, EventRequestStatusUpdateRequest dto) {
        log.debug("Изменение статуса на {}  заявок на участие в событии с id = {} пользователя с id = {}", dto.getStatus(), eventId, userId);
        userService.exists(userId);
        Event event = get(eventId);
        List<EventRequest> requests = eventRequestService.findAllById(dto.getRequestIds());
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = EventRequestStatusUpdateResult.builder().build();

        List<ParticipationRequestDto> confirmedList = new ArrayList<>();
        List<ParticipationRequestDto> rejectedList = new ArrayList<>();

        if (requests.isEmpty()) {
            return eventRequestStatusUpdateResult;
        }

        if (!requests.stream()
                .map(EventRequest::getStatus)
                .allMatch(Status.PENDING::equals)) {
            throw new ConflictException("Only requests that are pending can be changed.");
        }

        if (requests.size() != dto.getRequestIds().size()) {
            throw new ConflictException("Some requests not found.");
        }

        int limitParticipants = event.getParticipantLimit();
        if (limitParticipants == 0 || !event.getRequestModeration()) {
            return eventRequestStatusUpdateResult;
        }

        Long countParticipants = eventRequestService.countByEventIdAndStatus(event.getId(), CONFIRMED);
        if (countParticipants >= limitParticipants) {
            throw new ConflictException("The participant limit has been reached.");
        }
        if (dto.getStatus().equals(REJECTED)) {
            for (EventRequest eventRequest : requests) {
                eventRequest.setStatus(REJECTED);
                rejectedList.add(EventRequestMapper.toParticipation(eventRequest));
            }
            eventRequestStatusUpdateResult.setRejectedRequests(rejectedList);

        } else {
            for (EventRequest eventRequest : requests) {
                if (countParticipants < limitParticipants) {
                    eventRequest.setStatus(CONFIRMED);
                    confirmedList.add(EventRequestMapper.toParticipation(eventRequest));
                    countParticipants++;
                } else {
                    eventRequest.setStatus(REJECTED);
                    rejectedList.add(EventRequestMapper.toParticipation(eventRequest));
                }
            }
            eventRequestStatusUpdateResult.setConfirmedRequests(confirmedList);
            eventRequestStatusUpdateResult.setRejectedRequests(rejectedList);
        }
        eventRequestService.saveAll(requests);
        return eventRequestStatusUpdateResult;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> get(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        log.debug("Поиск событий по параметрам: users = {}, states = {}, categories = {}, rangeStart = {}, rangeEnd = {}", users, states, categories, rangeStart, rangeEnd);
        checkStartIsBeforeEnd(rangeStart, rangeEnd);
        checkStates(states);
        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size);
        List<Event> events = eventRepository.findEvents(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                pageable);

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> views = getViews(events);

        List<EventFullDto> eventFullDtoList = events.stream()
                .map(EventMapper::toFullDto)
                .collect(Collectors.toList());


        for (EventFullDto dto : eventFullDtoList) {
            dto.setViews(views.getOrDefault(dto.getId(), 0L));
            dto.setConfirmedRequests(confirmedRequests.getOrDefault(dto.getId(), 0L));
        }
        return eventFullDtoList;
    }

    @Override
    @Transactional
    public EventFullDto update(Long eventId, UpdateEventAdminRequest dto) {
        log.debug("Обновление информации о событии с id = {}, данные для обновления {}", eventId, dto);
        Event event = get(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationRequestException("The start date of the edited event must be no earlier than one hour from the date of publication");
        }
        if (dto.getStateAction() != null) {
            if (dto.getStateAction() == PUBLISH_EVENT) {
                if (event.getState() != PENDING) {
                    throw new ConflictException("Cannot publish the event because it's not in the right state: " + event.getState());
                }
                event.setPublishedOn(LocalDateTime.now());
                event.setState(PUBLISHED);
            } else if (dto.getStateAction() == REJECT_EVENT) {
                if (event.getState() == PUBLISHED) {
                    throw new ConflictException("An event can only be reject if it hasn't been published yet");
                } else {
                    event.setState(CANCELED);
                }
            }
        }
        if (dto.getEventDate() != null
                && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationRequestException("Event date must not be before 2 hours from current time.");
        }

        setValue(event, dto.getAnnotation(), dto.getCategory(), dto.getDescription(), dto.getEventDate(), dto.getLocation(), dto.getPaid(), dto.getParticipantLimit(), dto.getRequestModeration());
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }

        EventFullDto eventFullDto = toFullDto(eventRepository.save(event));
        return fillViewAndConfirmedRequests(eventFullDto, event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> get(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from, Integer size, HttpServletRequest request) {
        log.debug("Вывод опубликованных событий с параметрами text = {}, categoriesId = {}, paid = {}, rangeStart = {}, rangeEnd = {}, onlyAvailable = {}, sort = {}, from = {}, size = {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        statsClient.save("ewm-main-service", request.getRequestURI(), request.getRemoteAddr());

        checkStartIsBeforeEnd(rangeStart, rangeEnd);

        List<Event> events = eventRepository.findPublishedEvents(
                text,
                categories,
                paid,
                rangeStart != null ? rangeStart : LocalDateTime.now(),
                rangeEnd,
                PageRequest.of(from / size, size));

        List<EventShortDto> eventShortDtoList = convertToShortDtoList(events);

        Map<Long, Integer> eventsParticipantLimit = new HashMap<>();
        events.forEach(event -> eventsParticipantLimit.put(event.getId(), event.getParticipantLimit()));

        if (onlyAvailable) {
            eventShortDtoList = eventShortDtoList.stream()
                    .filter(eventShort -> (eventsParticipantLimit.get(eventShort.getId()) == 0 ||
                            eventsParticipantLimit.get(eventShort.getId()) > eventShort.getConfirmedRequests()))
                    .collect(Collectors.toList());
        }

        if (sort != null) {
            switch (sort) {
                case EVENT_DATE:
                    eventShortDtoList.sort(Comparator.comparing(EventShortDto::getEventDate));
                    break;

                case VIEWS:
                    eventShortDtoList.sort(Comparator.comparing(EventShortDto::getViews));
                    break;
                default:
                    throw new ValidationRequestException("Parameter sort is not valid");
            }
        }

        return eventShortDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto get(Long id, HttpServletRequest request) {
        log.debug("Получение информации об опубликованном событии c id = {}", id);
        statsClient.save("ewm-main-service", request.getRequestURI(), request.getRemoteAddr());
        Event event = get(id);
        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException("Event", id);
        }

        EventFullDto eventFullDto = toFullDto(event);
        return fillViewAndConfirmedRequests(eventFullDto, event);
    }

    @Override
    @Transactional(readOnly = true)
    public Event getByLocationId(Long locId) {
        return eventRepository.getFirstByLocation_Id(locId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventByLocationInAndState(List<Location> locations, State state) {
        log.debug("Получение списка событий в локациях {} со статусом {}", locations, state);
        List<Event> events = eventRepository.getEventByLocationInAndState(locations, state);

        return convertToShortDtoList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> get(Float lat, Float lon, Float radius, Integer from, Integer size) {
        List<Location> locations = locationService.get(lat, lon, radius, from, size);
        return getEventByLocationInAndState(locations, PUBLISHED);
    }

    /**
     * Получение статистики просмотром события
     *
     * @param events список событий
     * @return Map<Long, Long>, id события, количество просмотров
     */

    private Map<Long, Long> getViews(List<Event> events) {
        log.debug("Отправлен запрос на получение статистики неуникальных посещений для списка событий.");

        Map<Long, Long> views = new HashMap<>();
        if (events.isEmpty()) {
            return views;
        }
        //Только опубликованные
        List<Event> publishedEvents = getPublished(events);

        Optional<LocalDateTime> minDate = publishedEvents.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        if (minDate.isPresent()) {
            LocalDateTime start = minDate.get();
            LocalDateTime end = LocalDateTime.now();
            List<String> uris = publishedEvents.stream()
                    .map(Event::getId)
                    .map(id -> ("/events/" + id))
                    .collect(Collectors.toList());

            List<StatsDto> stats = statsClient.get(start, end, uris, true);
            stats.forEach(stat -> {
                Long eventId = Long.parseLong(stat.getUri()
                        .split("/", 0)[2]);
                views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
            });
        }
        log.debug("Данные статистики {} ", views);
        return views;
    }

    /**
     * Получение подтверждённых запросов
     *
     * @param events список событий
     * @return Map<Long, Long>, id события, количество запросов
     */

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        log.debug("Получаем список подтверждённых запросов {}", events);
        //Только опубликованные
        List<Event> publishedEvents = getPublished(events);

        Map<Long, Long> confirmedRequests = eventRequestService.findAllByEventInAndStatus(publishedEvents, CONFIRMED)
                .stream()
                .collect(Collectors.groupingBy(eventRequest -> eventRequest.getEvent().getId(), Collectors.counting()));
        log.debug("Список подтверждённых запросов {}", confirmedRequests);
        return confirmedRequests;
    }

    /**
     * Фильтрация опубликованных запросов
     *
     * @param events список событий
     * @return список опубликованных событий
     */

    private List<Event> getPublished(List<Event> events) {
        return events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());
    }

    /**
     * Заполнение EventFullDto данными о просмотрах и количестве подтверждённых запросов
     *
     * @param dto   EventFullDto
     * @param event модель события
     * @return заполненное EventFullDto
     */
    private EventFullDto fillViewAndConfirmedRequests(EventFullDto dto, Event event) {
        Map<Long, Long> confirmedRequests = getConfirmedRequests(List.of(event));
        Map<Long, Long> views = getViews(List.of(event));
        dto.setViews(views.getOrDefault(dto.getId(), 0L));
        dto.setConfirmedRequests(confirmedRequests.getOrDefault(dto.getId(), 0L));
        return dto;
    }

    /**
     * Проверка, что состояние принадлежит enum State
     *
     * @param states список строк
     */

    private void checkStates(List<String> states) {
        if (states != null) {
            for (String state : states) {
                try {
                    State.valueOf(state);
                } catch (IllegalArgumentException e) {
                    throw new ValidationRequestException("Wrong states!");
                }
            }
        }
    }

    /**
     * Проверка, что дата rangeStart ранее даты rangeEnd
     *
     * @param rangeStart дата начала
     * @param rangeEnd   дата конца
     */

    private void checkStartIsBeforeEnd(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationRequestException("Start date later than end date");
        }
    }

    /**
     * Метод заполнения события данными из запроса на изменение
     *
     * @param event событие
     * @param dto   запрос на изменение события
     */

    private void updateEventFromUserRequest(Event event, UpdateEventUserRequest dto) {
        log.debug("Обновляем событие");

        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        setValue(event, dto.getAnnotation(), dto.getCategory(), dto.getDescription(), dto.getEventDate(), dto.getLocation(), dto.getPaid(), dto.getParticipantLimit(), dto.getRequestModeration());
        if (dto.getStateAction() != null) {
            if (dto.getStateAction() == CANCEL_REVIEW)
                event.setState(CANCELED);
            if (dto.getStateAction() == SEND_TO_REVIEW)
                event.setState(PENDING);
        }
    }

    /**
     * Перекладывание одноимённых аттрибутов
     *
     * @param event
     * @param annotation
     * @param category
     * @param description
     * @param eventDate
     * @param location
     * @param paid
     * @param participantLimit
     * @param requestModeration
     */

    private void setValue(Event event, String annotation, Long category, String description, LocalDateTime eventDate, Location location, Boolean paid, Integer participantLimit, Boolean requestModeration) {
        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (category != null) {
            event.setCategory(categoryService.getCategory(category));
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (eventDate != null) {
            event.setEventDate(eventDate);
        }
        if (location != null) {
            event.setLocation(locationService.getOrSave(location.getLat(), location.getLon()));
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
    }

    /**
     * Заполнение необходимых данных для конвертации модели Event в EventShortDto
     *
     * @param events список событий
     * @return список EventShortDto
     */

    private List<EventShortDto> convertToShortDtoList(List<Event> events) {

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> views = getViews(events);

        List<EventShortDto> eventShortDtoList = events.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());

        for (EventShortDto dto : eventShortDtoList) {
            dto.setViews(views.getOrDefault(dto.getId(), 0L));
            dto.setConfirmedRequests(confirmedRequests.getOrDefault(dto.getId(), 0L));
        }
        return eventShortDtoList;
    }
}
