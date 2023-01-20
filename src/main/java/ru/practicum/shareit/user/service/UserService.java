package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> findAll();

    UserResponseDto findOne(Long userId);

    UserResponseDto create(CreateUserRequestDto createUserRequestDto);

    UserResponseDto update(Long userId, UpdateUserRequestDto createUserRequestDto);

    void removeById(Long userId);
}
