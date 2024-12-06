package ru.practicum.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.CreateHitDto;

import java.util.List;

public interface StatClient {

    ResponseEntity<?> hit(CreateHitDto createHitDto);

    ResponseEntity<?> getStats(String start, String end, List<String> uris, Boolean unique);
}
