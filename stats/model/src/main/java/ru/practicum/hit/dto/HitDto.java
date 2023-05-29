package ru.practicum.hit.dto;

import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


@Builder
@Getter
public class HitDto {
    @NotNull
    private String app;
    @NotNull
    private String uri;
    @NotNull
    private String ip;
    @NotNull
    private String timestamp;
}
