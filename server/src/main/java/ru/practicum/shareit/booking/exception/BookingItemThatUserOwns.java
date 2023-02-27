package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingItemThatUserOwns extends RuntimeException {
    public BookingItemThatUserOwns(User user, Item item) {
        super(String.format("User with id `%d` tried to book an item with id `%d`.", user.getId(), item.getId()));
    }
}
