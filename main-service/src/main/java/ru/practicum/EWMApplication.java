package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.client.RestStatClient;
import ru.practicum.client.StatClient;

@SpringBootApplication
public class EWMApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EWMApplication.class, args);
        StatClient statClient = context.getBean(RestStatClient.class);
        System.out.println("Hello, World!");
    }
}