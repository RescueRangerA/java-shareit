package ru.practicum.shareIt.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareIt.user.dto.CreateUserRequestDto;
import ru.practicum.shareIt.user.dto.UpdateUserRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");

        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(
            @PathVariable @Positive Long userId
    ) {
        log.info("Get user {}", userId);

        return userClient.findOne(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(
            @RequestBody @Valid CreateUserRequestDto createUserRequestDto
    ) {
        log.info("Crating user {}", createUserRequestDto);

        return userClient.create(createUserRequestDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @PathVariable @Positive Long userId,
            @RequestBody @Valid UpdateUserRequestDto updateUserRequestDto
    ) {
        log.info("Crating user with id {}, {}", userId, updateUserRequestDto);

        return userClient.update(userId, updateUserRequestDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(
            @PathVariable @Positive Long userId
    ) {
        log.info("Deleting user {}", userId);

        return userClient.removeById(userId);
    }
}
