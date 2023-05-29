package ru.practicum.service.stats;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.repository.StatsRepository;
import ru.practicum.stats.dto.StatsDto;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsRepository statsRepository;

    @Override
    public List<StatsDto> get(String start, String end, String[] uris, boolean unique) {
        LocalDateTime startDate = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), FORMATTER);

        List<StatsDto> statsDtoList;
        if (uris == null) {
            if (unique) {
                log.debug("Получаем стастику за период {} - {} c учётом уникальных посещений", startDate, endDate);
                statsDtoList = statsRepository.findAllByDateAndUniqueIp(startDate, endDate);
            } else {
                log.debug("Получаем стастику за период {} - {}", startDate, endDate);
                statsDtoList = statsRepository.findAllByDate(startDate, endDate);
            }
        } else {
            if (unique) {
                log.debug("Получаем стастику за период {} - {} c учётом уникальных посещений по адресам {}", startDate, endDate, uris);
                statsDtoList = statsRepository.findAllByUrisAndUniqueIp(uris, startDate, endDate);
            } else {
                log.debug("Получаем стастику за период {} - {}, по адресам {}", startDate, endDate, uris);
                statsDtoList = statsRepository.findAllByUris(uris, startDate, endDate);
            }
        }
        return statsDtoList;
    }
}
