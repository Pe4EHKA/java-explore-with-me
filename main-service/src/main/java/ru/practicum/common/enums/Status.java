package ru.practicum.common.enums;

public enum Status {
    CONFIRMED,
    PENDING,
    REJECTED,
    CANCELED;

    public static Status from(String status) {
            for (Status s: Status.values()) {
                if (s.toString().equalsIgnoreCase(status)) {
                    return s;
                }
            }
            return null;
    }
}
