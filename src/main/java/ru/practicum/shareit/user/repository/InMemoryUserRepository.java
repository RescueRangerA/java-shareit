package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityIsNotFoundException;
import ru.practicum.shareit.exceptions.UserEmailDuplication;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> idStorage;

    private final Map<String, User> emailStorage;

    private final AtomicLong readyIndex;

    public InMemoryUserRepository() {
        this.idStorage = new HashMap<>();
        this.emailStorage = new HashMap<>();
        this.readyIndex = new AtomicLong();
    }

    @Override
    public void deleteById(Long id) {
        User removedUser = idStorage.remove(id);

        if (removedUser != null) {
            emailStorage.remove(removedUser.getEmail());
        }
    }

    @Override
    public Iterable<User> findAll() {
        return idStorage.values();
    }

    @Override
    public User save(User user) {
        if (emailStorage.get(user.getEmail()) != null) {
            throw new UserEmailDuplication();
        }

        if (user.getId() == null) {
            user.setId(readyIndex.incrementAndGet());

            emailStorage.put(user.getEmail(), user);
            idStorage.put(user.getId(), user);

            return user;
        } else {
            User existingUser = idStorage.get(user.getId());

            if (existingUser == null) {
                throw new EntityIsNotFoundException(User.class, user.getId());
            }

            if (user.getName() != null) {
                existingUser.setName(user.getName());
            }

            if (user.getEmail() != null) {
                emailStorage.remove(existingUser.getEmail());
                existingUser.setEmail(user.getEmail());
                emailStorage.put(existingUser.getEmail(), existingUser);
            }

            idStorage.put(existingUser.getId(), existingUser);

            return existingUser;
        }
    }

    @Override
    public User findOne(Long userId) {
        return idStorage.get(userId);
    }
}
