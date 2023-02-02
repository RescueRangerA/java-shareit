package ru.practicum.shareit.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.security.user.AuthenticatedUser;
import ru.practicum.shareit.security.user.ExtendedUserDetails;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
public class AuthenticatedUserDetailsService implements ExtendedUserDetailsService {
    private final UserRepository userRepository;

    public AuthenticatedUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new AuthenticatedUser(
                this.userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ExtendedUserDetails loadUserById(Long id) {
        return new AuthenticatedUser(
                this.userRepository
                        .findById(id)
                        .orElseThrow(() -> new ExtendedEntityNotFoundException(User.class, id))
        );
    }
}
