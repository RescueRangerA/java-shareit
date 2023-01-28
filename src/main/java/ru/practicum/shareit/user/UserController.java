package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable @Positive Long userId) {
        return userService.findOne(userId);
    }

    @PostMapping
    public UserResponseDto createUser(@Valid @RequestBody CreateUserRequestDto createUserRequestDto) {
        return userService.create(createUserRequestDto);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@PathVariable @Positive Long userId, @Valid @RequestBody UpdateUserRequestDto updateUserRequestDto) {
        return userService.update(userId, updateUserRequestDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @Positive Long userId) {
        userService.removeById(userId);
    }
}
