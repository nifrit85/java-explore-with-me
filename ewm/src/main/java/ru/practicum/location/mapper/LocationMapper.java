package ru.practicum.location.mapper;

import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.LocationNewDto;
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

    public static Location newToModel(LocationNewDto dto) {
        return Location.builder()
                .lat(dto.getLat())
                .lon(dto.getLon())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public static LocationFullDto ToFullDto(Location model) {
        return LocationFullDto.builder()
                .id(model.getId())
                .lat(model.getLat())
                .lon(model.getLon())
                .name(model.getName())
                .description(model.getDescription())
                .build();
    }
}
