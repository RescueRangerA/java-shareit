package ru.practicum.shareit.security.facade;

import org.springframework.security.core.Authentication;
import ru.practicum.shareit.security.user.ExtendedUserDetails;
import ru.practicum.shareit.user.model.User;

public interface IAuthenticationFacade {
    Authentication getAuthentication();

    ExtendedUserDetails getCurrentUserDetails();

    User getCurrentUser();
}
