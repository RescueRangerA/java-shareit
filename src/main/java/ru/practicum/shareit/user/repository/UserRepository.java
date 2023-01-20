package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

public interface UserRepository {
    void deleteById(Long id);

    Iterable<User> findAll();

    User save(User user);

    User findOne(Long id);
}
