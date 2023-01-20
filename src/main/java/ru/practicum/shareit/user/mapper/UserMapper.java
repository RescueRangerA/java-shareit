package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);

    User toUser(Long userId, UpdateUserRequestDto user);

    User toUser(CreateUserRequestDto user);
}
