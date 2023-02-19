package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.booking.model.Booking;

public class ImmutableBookingStatus extends RuntimeException {
    public ImmutableBookingStatus(Booking booking) {
        super(String.format("Status of booking with id `%d` cannot be changed. Current status: %s", booking.getId(), booking.getStatus()));
    }
}
