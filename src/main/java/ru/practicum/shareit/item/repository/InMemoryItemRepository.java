package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityIsNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> storage;

    private final AtomicLong readyIndex;

    public InMemoryItemRepository() {
        storage = new LinkedHashMap<>();
        readyIndex = new AtomicLong();
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public Iterable<Item> findAll() {
        return storage.values();
    }

    @Override
    public Iterable<Item> findAllAvailableForUser(User user) {
        return storage
                .values()
                .stream()
                .filter((item -> item.getOwner().getId().equals(user.getId())))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(readyIndex.incrementAndGet());
            storage.put(item.getId(), item);

            return item;
        } else {
            Item existingItem = storage.get(item.getId());

            if (existingItem == null) {
                throw new EntityIsNotFoundException(Item.class, item.getId());
            }

            if (item.getName() != null) {
                existingItem.setName(item.getName());
            }

            if (item.getDescription() != null) {
                existingItem.setDescription(item.getDescription());
            }

            if (item.getAvailable() != null) {
                existingItem.setAvailable(item.getAvailable());
            }

            storage.put(existingItem.getId(), existingItem);

            return existingItem;
        }
    }

    @Override
    public Item findOne(Long id) {
        return storage.get(id);
    }

    @Override
    public Iterable<Item> findAllAvailableByNameOrDescriptionContainingCaseInsensitive(String query) {
        String lowerQuery = query.toLowerCase();

        return storage
                .values()
                .stream()
                .filter(Item::getAvailable)
                .filter((item -> item.getName().toLowerCase().contains(lowerQuery) || item.getDescription().toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());
    }
}
