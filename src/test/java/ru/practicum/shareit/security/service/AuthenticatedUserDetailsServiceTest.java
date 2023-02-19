package ru.practicum.shareit.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.security.user.AuthenticatedUser;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private ExtendedUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        this.userDetailsService = new AuthenticatedUserDetailsService(this.userRepository);
    }

    @Test
    void loadUserByUsername_whenUserNotFound_thenThrownException() {
        String username = "username";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username)
        );
    }

    @Test
    void loadUserByUsername_whenUserFound_thenReturnedUser() {
        String username = "username";
        User user = new User();
        UserDetails authenticatedUser = new AuthenticatedUser(user);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails actualAuthenticatedUser = userDetailsService.loadUserByUsername(username);

        assertThat(authenticatedUser, hasToString(actualAuthenticatedUser.toString()));
    }

    @Test
    void loadUserById_whenUserNotFound_thenThrownException() {
        Long userId =  0L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                ExtendedEntityNotFoundException.class,
                () -> userDetailsService.loadUserById(userId)
        );
    }

    @Test
    void loadUserById_whenUserFound_thenReturnedUser() {
        Long userId = 0L;
        User user = new User();
        UserDetails authenticatedUser = new AuthenticatedUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDetails actualAuthenticatedUser = userDetailsService.loadUserById(userId);

        assertThat(authenticatedUser, hasToString(actualAuthenticatedUser.toString()));
    }
}