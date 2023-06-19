package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@PropertySource("classpath:stats-application.properties")
public class StatClientImpl implements StatClient {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final WebClient webClient;

    public StatClientImpl(@Value("${statistics-server.url}") String baseurl) {

        webClient = WebClient.builder()
                .baseUrl(baseurl)
                .build();
    }

    @Override
    public void save(String app, String uri, String ip) {
        String timestamp = LocalDateTime.now().format(dateTimeFormatter);
        HitDto hitDto = HitDto.builder()
                .app(app)
                .ip(ip)
                .uri(uri)
                .timestamp(timestamp)
                .build();

        webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(hitDto))
                .retrieve()
                .bodyToMono(HitDto.class)
                .block();
    }

    @Override
    public List<StatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", start.format(dateTimeFormatter))
                        .queryParam("end", end.format(dateTimeFormatter))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToFlux(StatsDto.class)
                .collectList()
                .block();
    }
}
