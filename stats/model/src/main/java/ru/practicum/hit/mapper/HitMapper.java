package ru.practicum.hit.mapper;

import ru.practicum.hit.dto.HitDto;
import ru.practicum.hit.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HitMapper {
    private HitMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Hit toModel(HitDto dto) {
        return Hit.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(LocalDateTime.parse(dto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
