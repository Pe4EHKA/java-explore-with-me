package ru.practicum.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConditionsNotMetException(final ConditionsNotMetException e) {
        log.error(e.getMessage(), e);
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());

        return new ApiError(errors,
                HttpStatus.CONFLICT.toString(),
                "For the requested operation the conditions are not met.",
                e.getMessage());
    }

    @ExceptionHandler ({ConflictException.class, JdbcSQLIntegrityConstraintViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final RuntimeException e) {
        log.error(e.getMessage(), e);
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());

        return new ApiError(errors,
                HttpStatus.CONFLICT.toString(),
                "Constraint violated.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.error(e.getMessage(), e);
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());

        return new ApiError(errors,
                HttpStatus.NOT_FOUND.toString(),
                "Object not found.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        List<String> errors = new ArrayList<>();

        e.getBindingResult().getAllErrors()
                .forEach(error -> errors.add(error.getDefaultMessage()));

        return new ApiError(errors,
                HttpStatus.BAD_REQUEST.toString(),
                "For the requested operation the method arguments are not valid.",
                Objects.requireNonNull(e.getFieldError()).toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolation(final ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        List<String> errors = new ArrayList<>();

        e.getConstraintViolations()
                .forEach(error -> errors.add(error.getPropertyPath() + ": " + error.getMessage()));

        return new ApiError(errors,
                HttpStatus.BAD_REQUEST.toString(),
                "Incorrect request.",
                e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final BadRequestException e) {
        log.error(e.getMessage(), e);
        List<String> errors = List.of(e.getMessage());

        return new ApiError(errors,
                HttpStatus.BAD_REQUEST.toString(),
                "Invalid request parameters.",
                e.getMessage());
    }
}
