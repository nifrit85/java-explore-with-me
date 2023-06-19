package ru.practicum.category.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewCategoryDto {
    @NotBlank
    @Size(max = 50, min = 1)
    private String name;
}
