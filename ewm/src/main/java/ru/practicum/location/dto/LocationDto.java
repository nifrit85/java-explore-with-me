package ru.practicum.location.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class LocationDto {
    private Float lat;
    private Float lon;
}
