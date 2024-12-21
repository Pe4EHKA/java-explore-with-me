package ru.practicum.common.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
public class ApiError {

    private List<String> errors;

    private String status;

    private String reason;

    private String message;

    private String timestamp;

    public ApiError(List<String> errors, String status, String reason, String message) {
        this.errors = errors;
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
