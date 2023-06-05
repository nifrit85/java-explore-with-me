package ru.practicum.events.mapper;


import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.model.Event;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.users.mapper.UserMapper;


public class EventMapper {
    private EventMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static EventShortDto toShortDto(Event model) {
        return EventShortDto.builder()
                .annotation(model.getAnnotation())
                .category(CategoryMapper.toDto(model.getCategory()))
                .eventDate(model.getEventDate())
                .id(model.getId())
                .initiator(UserMapper.toShortDto(model.getInitiator()))
                .paid(model.getPaid())
                .title(model.getTitle())
                .build();
    }

    public static Event toModel(NewEventDto dto) {
        return Event.builder()
                .title(dto.getTitle())
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .location(dto.getLocation())
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .build();
    }

    public static EventFullDto toFullDto(Event model) {
        return EventFullDto.builder()
                .id(model.getId())
                .title(model.getTitle())
                .annotation(model.getAnnotation())
                .description(model.getDescription())
                .eventDate(model.getEventDate())
                .location(LocationMapper.toDto(model.getLocation()))
                .paid(model.getPaid())
                .participantLimit(model.getParticipantLimit())
                .requestModeration(model.getRequestModeration())
                .category(CategoryMapper.toDto(model.getCategory()))
                .createdOn(model.getCreatedOn())
                .publishedOn(model.getPublishedOn())
                .initiator(UserMapper.toShortDto(model.getInitiator()))
                .state(model.getState())
                .build();
    }
}
