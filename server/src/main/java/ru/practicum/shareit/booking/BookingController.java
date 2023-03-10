package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.SearchBookingStatus;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.extension.CustomPageableParameters;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseBookingDto create(
                @RequestBody CreateBookingDto createBookingDto
    ) {
        return this.bookingService.create(createBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved
    ) {
        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;

        return this.bookingService.updateStatus(bookingId, newStatus);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto get(
            @PathVariable Long bookingId
    ) {
        return this.bookingService.findOne(bookingId);
    }

    @GetMapping
    public List<ResponseBookingDto> getAllByStatus(
            @RequestParam(defaultValue = SearchBookingStatus.DEFAULT, required = false) SearchBookingStatus state,
            @RequestParam(required = false, defaultValue = "0") Long from,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return this.bookingService.findAllBookedByCurrentUserByStatusOrderByDateDesc(
                state,
                CustomPageableParameters.of(from, size)
        );
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getAllForCurrentUserByStatus(
            @RequestParam(defaultValue = SearchBookingStatus.DEFAULT, required = false) SearchBookingStatus state,
            @RequestParam(required = false, defaultValue = "0") Long from,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return this.bookingService.findAllForCurrentUserItemsByStatusOrderByDateDesc(
                state,
                CustomPageableParameters.of(from, size)
        );
    }
}
