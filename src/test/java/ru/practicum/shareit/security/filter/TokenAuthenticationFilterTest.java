package ru.practicum.shareit.security.filter;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.security.exception.IncorrectAuthHeader;
import ru.practicum.shareit.security.service.ExtendedUserDetailsService;
import ru.practicum.shareit.security.user.AuthenticatedUser;
import ru.practicum.shareit.user.model.User;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TokenAuthenticationFilterTest {

    private static final String HEADER_NAME = "X-Sharer-User-Id";

    @Mock
    private ExtendedUserDetailsService userDetailsService;

    @Mock
    private HandlerExceptionResolver resolver;

    private TokenAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        this.filter = new TokenAuthenticationFilter(
                userDetailsService,
                resolver
        );
    }

    @SneakyThrows
    @Test
    void doFilterInternal_whenHeaderIsMissing_thenResolveException() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(req, res, chain);
        verify(resolver).resolveException(eq(req), eq(res), eq(null), ArgumentMatchers.any(IncorrectAuthHeader.class));
    }

    @SneakyThrows
    @Test
    void doFilterInternal_whenHeaderIsCorrupted_thenResolveException() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        req.addHeader(HEADER_NAME, "header");
        filter.doFilterInternal(req, res, chain);
        verify(resolver).resolveException(eq(req), eq(res), eq(null), ArgumentMatchers.any(IncorrectAuthHeader.class));
    }

    @SneakyThrows
    @Test
    void doFilterInternal_whenUserNotFound_thenResolveException() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        Long userId = 1L;
        req.addHeader(HEADER_NAME, userId);
        when(userDetailsService.loadUserById(userId)).thenThrow(ExtendedEntityNotFoundException.class);
        filter.doFilterInternal(req, res, chain);
        verify(resolver).resolveException(eq(req), eq(res), eq(null), ArgumentMatchers.any(ExtendedEntityNotFoundException.class));
    }

    @SneakyThrows
    @Test
    void doFilterInternal_whenAuthenticated_thenExpectNoInteractionsWithResolver() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        Long userId = 1L;
        req.addHeader(HEADER_NAME, userId);
        when(userDetailsService.loadUserById(userId)).thenReturn(new AuthenticatedUser(new User(userId, "", "")));
        filter.doFilterInternal(req, res, chain);
        verifyNoInteractions(resolver);
    }
}