package ru.practicum.shareIt.exception;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    void handleException() {
        Exception exception = mock(Exception.class);
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        controllerAdvice.handleException(req, res, null, exception);
        assertThat(res.getStatus(), equalTo(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
    }
}