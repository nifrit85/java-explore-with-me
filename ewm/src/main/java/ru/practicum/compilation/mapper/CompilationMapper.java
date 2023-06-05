package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.events.mapper.EventMapper;

import java.util.Collections;
import java.util.stream.Collectors;

public class CompilationMapper {

    private CompilationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static CompilationDto toDto(Compilation model) {
        return CompilationDto.builder()
                .id(model.getId())
                .events(model.getEvents() != null ?
                        model.getEvents().stream().map(EventMapper::toShortDto).collect(Collectors.toSet())
                        : Collections.emptySet())
                .pinned(model.getPinned())
                .title(model.getTitle())
                .build();
    }

    public static Compilation toModel(NewCompilationDto dto) {
        return Compilation.builder()
                .pinned(dto.getPinned() != null ? dto.getPinned() : false)
                .title(dto.getTitle())
                .build();
    }
}
