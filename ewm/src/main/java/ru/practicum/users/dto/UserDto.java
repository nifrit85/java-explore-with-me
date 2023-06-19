package ru.practicum.users.dto;


import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto {
    @NotNull
    @Email
    private String email;
    private Long id;
    @NotNull
    private String name;
}
