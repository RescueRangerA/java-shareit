package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

public interface ItemWithBookingProjection {
    Item getItem();

    Booking getLastBooking();

    Booking getNextBooking();
}
