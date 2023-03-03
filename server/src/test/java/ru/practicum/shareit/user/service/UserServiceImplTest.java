package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import static org.hamcrest.MatcherAssert.assertThat;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, modelMapper);
    }

    @Test
    void findAll_whenInvoked_thenUsersCollectionInResult() {
        User user = new User();
        UserResponseDto expectedUser = new UserResponseDto();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(modelMapper.toUserResponseDto(user)).thenReturn(expectedUser);

        List<UserResponseDto> actualUsers = userService.findAll();

        Mockito.verify(userRepository).findAll();
        assertThat(actualUsers.size(), equalTo(1));
        assertThat(actualUsers.get(0), equalTo(expectedUser));
    }

    @Test
    void findAll_whenInvokedForEmpty_thenReturnedEmptyCollection() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserResponseDto> actualUsers = userService.findAll();

        verify(userRepository, Mockito.times(1)).findAll();
        assertThat(actualUsers.size(), equalTo(0));
    }

    @Test
    void findOne_whenUserFound_thenReturnedUserDto() {
        Long userId = 0L;
        User user = new User();
        UserResponseDto expectedUser = new UserResponseDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.toUserResponseDto(user)).thenReturn(expectedUser);

        UserResponseDto actualUser = userService.findOne(userId);
        assertThat(actualUser, equalTo(expectedUser));
    }

    @Test
    void findOne_whenUserNotFound_thenThrownException() {
        Long userId = 0L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ExtendedEntityNotFoundException.class, () -> userService.findOne(userId));
    }

    @Test
    void create_whenInvoked_thenReturnedUserDto() {
        User userToSave = new User();
        CreateUserRequestDto createUserDto = new CreateUserRequestDto();
        UserResponseDto expectedUserDto = new UserResponseDto();

        when(modelMapper.toUser(createUserDto)).thenReturn(userToSave);
        when(userRepository.save(userToSave)).thenReturn(userToSave);
        when(modelMapper.toUserResponseDto(userToSave)).thenReturn(expectedUserDto);

        UserResponseDto actualUserDto = userService.create(createUserDto);

        verify(userRepository).save(userToSave);
        assertThat(actualUserDto, equalTo(expectedUserDto));
    }

    @Test
    void update_whenUserNotFound_thenThrownException() {
        Long userId = 0L;
        UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                ExtendedEntityNotFoundException.class,
                () -> userService.update(
                        userId,
                        updateUserRequestDto
                )
        );
    }

    @Test
    void update_whenUserFound_thenUpdateFields() {
        Long userId = 0L;

        User oldUser = new User();
        oldUser.setId(userId);
        oldUser.setUsername("user1");
        oldUser.setEmail("a@a.com");

        UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto();
        updateUserRequestDto.setName("user2");
        updateUserRequestDto.setEmail("b@b.com");

        UserResponseDto expectedUserResponseDto = new UserResponseDto();
        expectedUserResponseDto.setId(userId);
        expectedUserResponseDto.setName("user2");
        expectedUserResponseDto.setEmail("b@b.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(modelMapper.toUserResponseDto(oldUser)).thenReturn(expectedUserResponseDto);

        UserResponseDto actualUserResponseDto = userService.update(
                userId,
                updateUserRequestDto
        );

        verify(modelMapper).toUserResponseDto(userArgumentCaptor.capture());
        User updatedOldUser = userArgumentCaptor.getValue();
        assertThat(updatedOldUser.getId(), equalTo(oldUser.getId()));
        assertThat(updatedOldUser.getUsername(), equalTo(updateUserRequestDto.getName()));
        assertThat(updatedOldUser.getEmail(), equalTo(updateUserRequestDto.getEmail()));

        assertThat(actualUserResponseDto, equalTo(expectedUserResponseDto));
    }

    @Test
    void removeById_whenUserFound_thenCheckIfDeleteByIdWasCalled() {
        Long userId = 0L;

        userService.removeById(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void removeById_whenUserNotFound_thenThrownException() {
        Long userId = 0L;

        Mockito
                .doThrow(EmptyResultDataAccessException.class)
                .when(userRepository)
                .deleteById(userId);

        assertThrows(
                ExtendedEntityNotFoundException.class,
                () -> userService.removeById(userId)
        );
    }
}