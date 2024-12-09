package ru.practicum.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HitStats {
    private String app;
    private String uri;
    private Long hits;
}
