package ru.practicum.shareit.user.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(mapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findOne(Long userId) {
        return mapper.toUserResponseDto(
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ExtendedEntityNotFoundException(User.class, userId))
        );
    }

    @Override
    @Transactional
    public UserResponseDto create(CreateUserRequestDto createUserRequestDto) {
        return mapper.toUserResponseDto(userRepository.save(mapper.toUser(createUserRequestDto)));
    }

    @Override
    @Transactional
    public UserResponseDto update(Long userId, UpdateUserRequestDto updateUserRequestDto) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ExtendedEntityNotFoundException(User.class, userId));

        if (updateUserRequestDto.getName() != null) {
            user.setUsername(updateUserRequestDto.getName());
        }

        if (updateUserRequestDto.getEmail() != null) {
            user.setEmail(updateUserRequestDto.getEmail());
        }

        return mapper.toUserResponseDto(user);
    }

    @Override
    @Transactional
    public void removeById(Long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException exception) {
            throw new ExtendedEntityNotFoundException(User.class, userId);
        }
    }
}
