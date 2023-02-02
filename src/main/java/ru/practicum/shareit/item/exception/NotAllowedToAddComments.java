package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class NotAllowedToAddComments extends RuntimeException {
    public NotAllowedToAddComments(User user, Item item) {
        super(String.format("User with id `%d` is not allowed to add comments to item with id `%d", user.getId(), item.getId()));
    }
}
