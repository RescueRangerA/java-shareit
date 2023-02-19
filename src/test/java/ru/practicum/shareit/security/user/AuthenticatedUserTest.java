package ru.practicum.shareit.security.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;

import static org.hamcrest.Matchers.*;

class AuthenticatedUserTest {

    private AuthenticatedUser extendedUserDetails;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "user", "a@a.com");
        extendedUserDetails = new AuthenticatedUser(user);
    }

    @Test
    void getId() {
        assertThat(extendedUserDetails.getId(), equalTo(1L));
    }

    @Test
    void getAuthorities_whenInvoked_thenAlwaysReturnsEmptyCollection() {
        assertThat(extendedUserDetails.getAuthorities(), equalTo(Collections.emptyList()));
    }

    @Test
    void getPassword_whenInvoked_thenAlwaysReturnsStringSecret() {
        assertThat(extendedUserDetails.getPassword(), equalTo("secret"));
    }

    @Test
    void getUsername_whenInvoked_thenReturnedUsername() {
        assertThat(extendedUserDetails.getUsername(), equalTo("user"));
    }

    @Test
    void isAccountNonExpired_whenInvoked_thenAlwaysReturnsTrue() {
        assertThat(extendedUserDetails.isAccountNonExpired(), equalTo(true));
    }

    @Test
    void isAccountNonLocked_whenInvoked_thenAlwaysReturnsTrue() {
        assertThat(extendedUserDetails.isAccountNonLocked(), equalTo(true));
    }

    @Test
    void isCredentialsNonExpired_whenInvoked_thenAlwaysReturnsTrue() {
        assertThat(extendedUserDetails.isCredentialsNonExpired(), equalTo(true));
    }

    @Test
    void isEnabled_whenInvoked_thenAlwaysReturnsTrue() {
        assertThat(extendedUserDetails.isEnabled(), equalTo(true));
    }
}