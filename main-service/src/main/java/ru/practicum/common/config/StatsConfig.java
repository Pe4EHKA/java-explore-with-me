package ru.practicum.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.practicum.client.RestStatClient;
import ru.practicum.client.StatClient;

@Configuration
public class StatsConfig {

    @Value("${client.url}")
    private String url;

    @Bean
    StatClient statClient(RestClient restClient) {
        return new RestStatClient(restClient, url);
    }
}
