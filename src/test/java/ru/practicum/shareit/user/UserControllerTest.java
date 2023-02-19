package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsers_whenInvoked_thenReturnedUserDtos() {
        List<UserResponseDto> expectedUserDtos = List.of(new UserResponseDto());

        when(userService.findAll()).thenReturn(expectedUserDtos);

        List<UserResponseDto> actualUserDtos = userController.getAllUsers();
        assertThat(actualUserDtos, hasToString(expectedUserDtos.toString()));
    }

    @Test
    void getUser_whenInvoked_thenReturnedUserDto() {
        Long userId = 0L;
        UserResponseDto expectedUserDto = new UserResponseDto();

        when(userService.findOne(userId)).thenReturn(expectedUserDto);

        UserResponseDto actualUserDto = userController.getUser(userId);
        assertThat(actualUserDto, hasToString(expectedUserDto.toString()));
    }

    @Test
    void createUser_whenInvoked_thenReturnedUserDto() {
        UserResponseDto expectedUserDto = new UserResponseDto();
        CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto();

        when(userService.create(createUserRequestDto)).thenReturn(expectedUserDto);

        UserResponseDto actualUserDto = userController.createUser(createUserRequestDto);
        assertThat(actualUserDto, hasToString(expectedUserDto.toString()));
    }

    @Test
    void updateUser_whenInvoked_thenReturnedUserDto() {
        Long userId = 0L;
        UserResponseDto expectedUserDto = new UserResponseDto();
        UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto();

        when(userService.update(userId, updateUserRequestDto)).thenReturn(expectedUserDto);

        UserResponseDto actualUserDto = userController.updateUser(userId, updateUserRequestDto);
        assertThat(actualUserDto, hasToString(expectedUserDto.toString()));
    }

    @Test
    void deleteUser_whenInvoked_thenCheckIfDeletionWasPerformed() {
        Long userId = 0L;
        userController.deleteUser(userId);
        verify(userService).removeById(userId);
    }
}