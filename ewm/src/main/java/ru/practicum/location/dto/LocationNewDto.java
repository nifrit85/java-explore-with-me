package ru.practicum.location.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LocationNewDto {
    @NotBlank
    @Size(max = 120)
    private String name;
    @NotNull
    private Float lat;
    @NotNull
    private Float lon;
    @NotBlank
    @Size(max = 7000)
    private String description;
}
