package ru.practicum.eventrequests.mapper;

import ru.practicum.eventrequests.dto.ParticipationRequestDto;
import ru.practicum.eventrequests.model.EventRequest;

public class EventRequestMapper {
    private EventRequestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static ParticipationRequestDto toParticipation(EventRequest model) {
        return ParticipationRequestDto.builder()
                .created(model.getCreated())
                .event(model.getEvent().getId())
                .id(model.getId())
                .requester(model.getRequester().getId())
                .status(model.getStatus())
                .build();
    }
}
