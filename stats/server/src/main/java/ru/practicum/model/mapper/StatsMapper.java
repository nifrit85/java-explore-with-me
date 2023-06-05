package ru.practicum.model.mapper;

import ru.practicum.dto.StatsDto;
import ru.practicum.model.Stats;

public class StatsMapper {
    private StatsMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static StatsDto toDto(Stats model) {
        return StatsDto.builder()
                .app(model.getApp())
                .uri(model.getUri())
                .hits(model.getHits())
                .build();
    }
}
