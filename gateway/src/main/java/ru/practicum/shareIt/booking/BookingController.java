package ru.practicum.shareIt.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareIt.booking.dto.CreateBookingDto;
import ru.practicum.shareIt.booking.dto.SearchBookingStatus;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    private static final String HEADER_NAME = "X-Sharer-User-Id";

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(HEADER_NAME) Long userId,
            @RequestBody @Valid CreateBookingDto createBookingDto
    ) {
        log.info("Creating booking {}, userId={}", createBookingDto, userId);

        return bookingClient.create(userId, createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(
            @RequestHeader(HEADER_NAME) Long userId,
            @PathVariable @Positive Long bookingId,
            @RequestParam Boolean approved
    ) {
        log.info("Set approve for booking {}, approved={}, userId={}", bookingId, approved, userId);

        return bookingClient.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(
            @RequestHeader(HEADER_NAME) Long userId,
            @PathVariable @Positive Long bookingId
    ) {
        log.info("Get booking {}, userId={}", bookingId, userId);

        return bookingClient.findOne(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByStatus(
            @RequestHeader(HEADER_NAME) Long userId,
            @RequestParam(defaultValue = SearchBookingStatus.DEFAULT, required = false) SearchBookingStatus state,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size
    ) {
        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);

        return bookingClient.findAllBookedByCurrentUserByStatusOrderByDateDesc(
                userId,
                state,
                from,
                size
        );
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllForCurrentUserByStatus(
            @RequestHeader(HEADER_NAME) Long userId,
            @RequestParam(defaultValue = SearchBookingStatus.DEFAULT, required = false) SearchBookingStatus state,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size
    ) {
        log.info("Get booking for current user with state {}, userId={}, from={}, size={}", state, userId, from, size);

        return bookingClient.findAllForCurrentUserItemsByStatusOrderByDateDesc(
                userId,
                state,
                from,
                size
        );
    }
}
