package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {
    void deleteById(Long id);

    List<Item> findAll();

    List<Item> findAllAvailableForUser(User user);

    Item save(Item item);

    Item findOne(Long id);

    List<Item> findAllAvailableByNameOrDescriptionContainingCaseInsensitive(String query);
}
