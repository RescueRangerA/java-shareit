package ru.practicum.shareit.security.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.security.exception.IncorrectAuthHeader;
import ru.practicum.shareit.security.user.ExtendedUserDetails;
import ru.practicum.shareit.security.service.ExtendedUserDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private static final String HEADER_NAME = "X-Sharer-User-Id";

    private final ExtendedUserDetailsService userDetailsService;

    private final HandlerExceptionResolver resolver;

    public TokenAuthenticationFilter(ExtendedUserDetailsService userDetailsService, HandlerExceptionResolver resolver) {
        this.userDetailsService = userDetailsService;
        this.resolver = resolver;
    }

    @Override
    @Transactional
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

        ExtendedUserDetails user;
        try {
            user = userDetailsService.loadUserById(userId);
        } catch (ExtendedEntityNotFoundException e) {
            resolver.resolveException(httpRequest, httpResponse, null, e);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,
                user.getPassword(),
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(httpRequest, httpResponse);
    }

}
