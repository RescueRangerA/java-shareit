package ru.practicum.shareit.exception.generic;


import javax.persistence.EntityNotFoundException;

public class ExtendedEntityNotFoundException extends EntityNotFoundException {
    public <T> ExtendedEntityNotFoundException(Class<T> entityClass, Long entityId) {
        super(String.format("Entity '%s' with id '%d' is not found", entityClass.getName(), entityId));
    }
}
