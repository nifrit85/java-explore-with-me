package ru.practicum.users.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewUserRequest {
    @NotBlank
    @Size(max = 254, min = 6)
    @Email
    private String email;
    @NotBlank
    @Size(max = 250, min = 2)
    private String name;
}
