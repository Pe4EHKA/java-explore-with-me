package ru.practicum.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class ApiError {
    private HttpStatus status;

    private String problem;

    private String message;

    private String stackTrace;
}
