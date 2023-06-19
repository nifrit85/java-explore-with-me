package ru.practicum.location.mapper;

import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;

public class LocationMapper {
    private LocationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static LocationDto toDto(Location model) {
        return LocationDto.builder()
                .lat(model.getLat())
                .lon(model.getLon())
                .build();
    }
}
