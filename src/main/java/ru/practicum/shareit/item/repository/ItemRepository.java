package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public interface ItemRepository {
    void deleteById(Long id);

    Iterable<Item> findAll();

    Iterable<Item> findAllAvailableForUser(User user);

    Item save(Item item);

    Item findOne(Long id);

    Iterable<Item> findAllAvailableByNameOrDescriptionContainingCaseInsensitive(String query);
}
