package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.hit.dto.HitDto;
import ru.practicum.stats.dto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatClientImpl implements StatClient {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
    private WebClient webClient;

    public StatClientImpl(String baseurl) {

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
    public List<StatsDto> get(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        StringBuilder uri = new StringBuilder("/stats?start=" +
                start.format(dateTimeFormatter) +
                "&end=" + end.format(dateTimeFormatter));
        if (uris != null) {
            uri.append("&uris=");
            for (String str : uris) {
                uri.append(str).append(",");

            }
            uri.deleteCharAt(uri.length() - 1);
        }

        if (unique != null) {
            uri.append("&unique=").append(unique);
        }

        return webClient.get()
                .uri(String.valueOf(uri))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(StatsDto.class)
                .collectList()
                .block();
    }
}
