package ru.practicum.location.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LocationDto {
    private Float lat;
    private Float lon;
}
