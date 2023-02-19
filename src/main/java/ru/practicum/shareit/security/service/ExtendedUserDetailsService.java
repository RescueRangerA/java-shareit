package ru.practicum.shareit.security.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.practicum.shareit.security.user.ExtendedUserDetails;

public interface ExtendedUserDetailsService extends UserDetailsService {
    ExtendedUserDetails loadUserById(Long id);
}
