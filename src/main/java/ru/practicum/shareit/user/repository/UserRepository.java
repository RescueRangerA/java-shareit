package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    void deleteById(Long id);

    List<User> findAll();

    User save(User user);

    User findOne(Long id);
}
