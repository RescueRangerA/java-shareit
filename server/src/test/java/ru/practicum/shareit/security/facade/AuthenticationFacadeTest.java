package ru.practicum.shareit.security.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.security.user.AuthenticatedUser;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFacadeTest {
    static class AuthenticationDummy implements Authentication {
        private AuthenticatedUser principal;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.emptyList();
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return principal;
        }

        public void setPrincipal(AuthenticatedUser principal) {
            this.principal = principal;
        }

        @Override
        public boolean isAuthenticated() {
            return false;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

        }

        @Override
        public String getName() {
            return null;
        }
    }


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    @Spy
    private AuthenticationFacade authenticationFacade;

    @Test
    void getCurrentUser_whenUserNotFound_thenThrownException() {
        Long userId = 0L;
        User user = new User(userId, "user", "a@a.com");
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);

        AuthenticationDummy authentication = new AuthenticationDummy();
        authentication.setPrincipal(authenticatedUser);
        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authenticationFacade.getCurrentUserDetails()).thenReturn(authenticatedUser);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                ExtendedEntityNotFoundException.class,
                () -> authenticationFacade.getCurrentUser()
        );
    }

    @Test
    void getCurrentUser_whenUserFound_thenReturnedUser() {
        Long userId = 0L;
        User user = new User(userId, "user", "a@a.com");
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);

        AuthenticationDummy authentication = new AuthenticationDummy();
        authentication.setPrincipal(authenticatedUser);
        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authenticationFacade.getCurrentUserDetails()).thenReturn(authenticatedUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User actualUser = authenticationFacade.getCurrentUser();
        assertThat(actualUser, equalTo(user));
    }
}