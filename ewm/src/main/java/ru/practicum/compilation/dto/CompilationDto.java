package ru.practicum.compilation.dto;


import lombok.*;
import ru.practicum.events.dto.EventShortDto;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompilationDto {
    private Set<EventShortDto> events;
    private Long id;
    @NotNull
    private Boolean pinned;
    @NotNull
    private String title;
}
