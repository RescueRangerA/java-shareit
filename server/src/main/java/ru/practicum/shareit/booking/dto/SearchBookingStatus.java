package ru.practicum.shareit.booking.dto;

public enum SearchBookingStatus {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static final String DEFAULT = "ALL";
}
