package ru.practicum.common.enums;

public enum State {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static State from(String state) {
        for (State s : State.values()) {
            if (s.toString().equalsIgnoreCase(state)) {
                return s;
            }
        }
        return null;
    }
}
