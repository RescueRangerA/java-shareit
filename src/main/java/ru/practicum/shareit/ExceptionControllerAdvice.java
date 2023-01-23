package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import ru.practicum.shareit.exceptions.EntityIsNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectAuthHeader;
import ru.practicum.shareit.exceptions.UserEmailDuplication;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {
    @ExceptionHandler
    public void handleEntityIsNotFoundException(final EntityIsNotFoundException e, HttpServletResponse response) throws IOException {
        logIfNeeded(e);
        response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler
    public void handleValidationException(final UserEmailDuplication e, HttpServletResponse response) throws IOException {
        logIfNeeded(e);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler
    public void handleValidationException(final AuthenticationException e, HttpServletResponse response) throws IOException {
        logIfNeeded(e);
        response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler
    public void handleValidationException(final IncorrectAuthHeader e, HttpServletResponse response) throws IOException {
        logIfNeeded(e);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler
    public ModelAndView handleValidationException(
            HttpServletRequest request,
            HttpServletResponse response,
            @Nullable Object handler,
            Exception e
    ) throws IOException {
        ModelAndView mav = (new DefaultHandlerExceptionResolver()).resolveException(request, response, handler, e);

        if (mav != null) {
            return mav;
        }

        logIfNeeded(e);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        return new ModelAndView();
    }

    protected void logIfNeeded(Exception e) {
        if (log != null && log.isWarnEnabled()) {
            log.warn(buildLogMessage(e));
        }
    }

    protected String buildLogMessage(Exception e) {
        return "Resolved [" + LogFormatUtils.formatValue(e, -1, true) + "]";
    }
}
