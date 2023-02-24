package ru.practicum.shareit.exception;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;

import java.util.Map;

import static org.mockito.Mockito.*;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class ExceptionControllerAdviceTest {

    private ExceptionControllerAdvice controllerAdvice;

    @BeforeEach
    void setUp() {
        controllerAdvice = new ExceptionControllerAdvice();
    }

    @Test
    void handleEnumTypeMismatch() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);

        when(exception.getName()).thenReturn("name");
        when(exception.getValue()).thenReturn("value");
        ResponseEntity<Map<String, Object>> response = controllerAdvice.handleEnumTypeMismatch(exception);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody().get("error"), equalTo("Unknown name: value"));

    }

    @SneakyThrows
    @Test
    void handleBadRequest() {
        Exception exception = mock(Exception.class);
        MockHttpServletResponse res = new MockHttpServletResponse();

        controllerAdvice.handleBadRequest(exception, res);
        assertThat(res.getStatus(), equalTo(HttpServletResponse.SC_BAD_REQUEST));
    }

    @SneakyThrows
    @Test
    void handleNotFound() {
        Exception exception = mock(Exception.class);
        MockHttpServletResponse res = new MockHttpServletResponse();

        controllerAdvice.handleNotFound(exception, res);
        assertThat(res.getStatus(), equalTo(HttpServletResponse.SC_NOT_FOUND));
    }

    @SneakyThrows
    @Test
    void handleValidationException() {
        AccessDeniedException accessDeniedException = mock(AccessDeniedException.class);
        MockHttpServletResponse res = new MockHttpServletResponse();

        controllerAdvice.handleValidationException(accessDeniedException, res);
        assertThat(res.getStatus(), equalTo(HttpServletResponse.SC_NOT_FOUND));
    }

    @SneakyThrows
    @Test
    void handleException() {
        Exception exception = mock(Exception.class);
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        controllerAdvice.handleException(req, res, null, exception);
        assertThat(res.getStatus(), equalTo(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
    }
}