package ru.practicum.shareit.exceptions;

public class IncorrectAuthHeader extends RuntimeException {
    public IncorrectAuthHeader() {
        super("Auth header is missing");
    }

    public IncorrectAuthHeader(String headerValue) {
        super(String.format("Corrupted auth header has been provided '%s'", headerValue));
    }
}
