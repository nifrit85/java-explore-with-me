package ru.practicum.users.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserShortDto {
    @NotNull
    private Long id;
    @NotNull
    private String name;
}
