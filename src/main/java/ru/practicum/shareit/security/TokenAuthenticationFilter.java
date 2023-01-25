package ru.practicum.shareit.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.practicum.shareit.exceptions.EntityIsNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectAuthHeader;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private static final String HEADER_NAME = "X-Sharer-User-Id";

    private final UserRepository userRepository;

    private final HandlerExceptionResolver resolver;

    public TokenAuthenticationFilter(UserRepository userRepository, HandlerExceptionResolver resolver) {
        this.userRepository = userRepository;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String accessToken = httpRequest.getHeader(HEADER_NAME);

        if (accessToken == null) {
            resolver.resolveException(httpRequest, httpResponse, null, new IncorrectAuthHeader());
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(accessToken);
        } catch (NumberFormatException e) {
            resolver.resolveException(httpRequest, httpResponse, null, new IncorrectAuthHeader(accessToken, e));
            return;
        }

        User user = userRepository.findOne(userId);
        if (user == null) {
            resolver.resolveException(httpRequest, httpResponse, null, new EntityIsNotFoundException(User.class, userId));
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,
                "",
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(httpRequest, httpResponse);
    }

}
