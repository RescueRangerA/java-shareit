package ru.practicum.shareit.exceptions;

public class EntityIsNotFoundException extends RuntimeException {
    public <T> EntityIsNotFoundException(Class<T> entityClass, Long entityId) {
        super(String.format("Entity '%s' with id '%d' is not found", entityClass.getName(), entityId));
    }
}
