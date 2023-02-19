package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.item.model.Item;

public class ItemIsUnavailable extends RuntimeException {
    public ItemIsUnavailable(Item item) {
        super(String.format("Item with id %d is unavailable", item.getId()));
    }
}
