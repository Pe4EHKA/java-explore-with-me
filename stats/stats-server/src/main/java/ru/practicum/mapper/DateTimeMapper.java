package ru.practicum.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeMapper {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime toLocalDateTime(String date) {
        return LocalDateTime.parse(date, formatter);
    }

    public static String toStringDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(formatter);
    }
}
