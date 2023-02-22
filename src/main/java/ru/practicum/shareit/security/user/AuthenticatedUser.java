package ru.practicum.shareit.security.user;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Collections;

@ToString
@EqualsAndHashCode
public class AuthenticatedUser implements ExtendedUserDetails {
    private final User user;

    public AuthenticatedUser(User user) {
        this.user = user;
    }

    @Override
    public Long getId() {
        return this.user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return "secret";
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
