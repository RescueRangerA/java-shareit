package ru.practicum.shareit.exceptions;

public class UserEmailDuplication extends RuntimeException {
    public UserEmailDuplication() {
        super("User email already exists");
    }
}
