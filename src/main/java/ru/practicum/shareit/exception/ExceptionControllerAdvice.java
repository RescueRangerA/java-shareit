package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import ru.practicum.shareit.booking.exception.BookingItemThatUserOwns;
import ru.practicum.shareit.booking.exception.ImmutableBookingStatus;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.item.exception.ItemIsUnavailable;
import ru.practicum.shareit.item.exception.NotAllowedToAddComments;
import ru.practicum.shareit.security.exception.IncorrectAuthHeader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {
    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleConflict(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", String.format("Unknown %s: %s", ex.getName(), ex.getValue()));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ImmutableBookingStatus.class, IncorrectAuthHeader.class, ItemIsUnavailable.class, NotAllowedToAddComments.class, ConstraintViolationException.class})
    public void handleBadRequest(final Exception e, HttpServletResponse response) throws IOException {
        logIfNeeded(e);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({ExtendedEntityNotFoundException.class, BookingItemThatUserOwns.class})
    public void handleNotFound(final Exception e, HttpServletResponse response) throws IOException {
        logIfNeeded(e);
        response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler
    public void handleValidationException(final AccessDeniedException e, HttpServletResponse response) throws IOException {
        logIfNeeded(e);
        response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler
    public ModelAndView handleException(
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
