package ru.practicum.shareit.security.facade;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.security.user.AuthenticatedUser;
import ru.practicum.shareit.security.user.ExtendedUserDetails;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {
    private final UserRepository userRepository;

    public AuthenticationFacade(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public ExtendedUserDetails getCurrentUserDetails() {
        return (AuthenticatedUser) getAuthentication().getPrincipal();
    }

    @Override
    public User getCurrentUser() {
        ExtendedUserDetails currentUser = this.getCurrentUserDetails();

        return userRepository
                .findById(currentUser.getId())
                .orElseThrow(() -> new ExtendedEntityNotFoundException(User.class, currentUser.getId()));
    }
}