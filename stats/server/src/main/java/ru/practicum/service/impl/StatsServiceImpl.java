package ru.practicum.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.StatsDto;
import ru.practicum.exception.ValidationRequestException;
import ru.practicum.model.Stats;
import ru.practicum.model.mapper.StatsMapper;
import ru.practicum.repository.StatsRepository;
import ru.practicum.service.StatsService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsRepository statsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StatsDto> get(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startDate = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), FORMATTER);
        if (startDate.isAfter(endDate))
            throw new ValidationRequestException("Date start must be before date end");
        List<Stats> statsList;
        if (uris == null) {
            if (unique) {
                log.debug("Получаем статистику за период {} - {} c учётом уникальных посещений", startDate, endDate);
                statsList = statsRepository.findAllByDateAndUniqueIp(startDate, endDate);
            } else {
                log.debug("Получаем статистику за период {} - {}", startDate, endDate);
                statsList = statsRepository.findAllByDate(startDate, endDate);
            }
        } else {
            if (unique) {
                log.debug("Получаем статистику за период {} - {} c учётом уникальных посещений по адресам {}", startDate, endDate, uris);
                statsList = statsRepository.findAllByUrisAndUniqueIp(uris, startDate, endDate);
            } else {
                log.debug("Получаем статистику за период {} - {}, по адресам {}", startDate, endDate, uris);
                statsList = statsRepository.findAllByUris(uris, startDate, endDate);
            }
        }
        return statsList
                .stream()
                .map(StatsMapper::toDto)
                .collect(Collectors.toList());
    }
}
