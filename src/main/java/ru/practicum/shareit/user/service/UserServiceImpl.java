package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityIsNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserResponseDto> findAll() {
        return StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto findOne(Long userId) {
        User user = userRepository.findOne(userId);

        if (user == null) {
            throw new EntityIsNotFoundException(User.class, userId);
        }

        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto create(CreateUserRequestDto createUserRequestDto) {
        return userMapper.toUserResponseDto(userRepository.save(userMapper.toUser(createUserRequestDto)));
    }

    @Override
    public UserResponseDto update(Long userId, UpdateUserRequestDto updateUserRequestDto) {
        return userMapper.toUserResponseDto(userRepository.save(userMapper.toUser(userId, updateUserRequestDto)));
    }

    @Override
    public void removeById(Long userId) {
        userRepository.deleteById(userId);
    }
}
