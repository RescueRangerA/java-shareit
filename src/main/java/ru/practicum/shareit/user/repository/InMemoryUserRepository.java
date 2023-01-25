package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityIsNotFoundException;
import ru.practicum.shareit.exceptions.UserEmailDuplication;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> idStorage;

    private final Set<String> emailUniqueStorage;

    private final AtomicLong readyIndex;

    public InMemoryUserRepository() {
        this.idStorage = new HashMap<>();
        this.emailUniqueStorage = new HashSet<>();
        this.readyIndex = new AtomicLong();
    }

    @Override
    public void deleteById(Long id) {
        User removedUser = idStorage.remove(id);

        if (removedUser != null) {
            emailUniqueStorage.remove(removedUser.getEmail());
        }
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(idStorage.values());
    }

    @Override
    public User save(User user) {
        if (emailUniqueStorage.contains(user.getEmail())) {
            throw new UserEmailDuplication();
        }

        if (user.getId() == null) {
            user.setId(readyIndex.incrementAndGet());

            emailUniqueStorage.add(user.getEmail());
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
                emailUniqueStorage.remove(existingUser.getEmail());
                existingUser.setEmail(user.getEmail());
                emailUniqueStorage.add(existingUser.getEmail());
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
