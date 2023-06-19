package ru.practicum.category.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CategoryDto {
    private Long id;
    @NotNull
    @Size(max = 50, min = 1)
    private String name;
}
