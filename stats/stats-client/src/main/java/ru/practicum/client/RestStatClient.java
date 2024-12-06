package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.CreateHitDto;

import java.util.List;

@Component
public class RestStatClient implements StatClient {
    private final RestClient restClient;
    private final String statUrl;

    @Autowired
    public RestStatClient(RestClient restClient, @Value("${client.url}") String statUrl) {
        this.restClient = restClient;
        this.statUrl = statUrl;
    }

    @Override
    public ResponseEntity<?> hit(CreateHitDto createHitDto) {
        String url = UriComponentsBuilder.fromHttpUrl(statUrl + "/hit")
                .build()
                .toUriString();
        try {
            return restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(createHitDto)
                    .retrieve()
                    .toEntity(CreateHitDto.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getStats(String start, String end, List<String> uris, Boolean unique) {
        String url = UriComponentsBuilder.fromHttpUrl(statUrl + "/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .build()
                .toUriString();

        try {
            return restClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(String.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
