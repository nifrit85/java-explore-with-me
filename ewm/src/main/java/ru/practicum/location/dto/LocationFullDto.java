package ru.practicum.location.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LocationFullDto {
    private Long id;
    @Size(max = 120)
    private String name;
    @NotNull
    private Float lat;
    @NotNull
    private Float lon;
    @Size(max = 7000)
    private String description;
}
