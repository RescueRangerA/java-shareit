package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {

    public UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public User toUser(Long userId, UpdateUserRequestDto user) {
        return new User(
                userId,
                user.getName(),
                user.getEmail()
        );
    }

    public User toUser(CreateUserRequestDto user) {
        return new User(
                null,
                user.getName(),
                user.getEmail()
        );
    }
}
