package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HitStatsDto {
    private String app;

    @JsonProperty("app")
    public String getApp() {
        return app;
    }

    private String uri;

    @JsonProperty("uri")
    public String getUri() {
        return uri;
    }

    private Long hits;

    @JsonProperty("hits")
    public Long getHits() {
        return hits;
    }
}
