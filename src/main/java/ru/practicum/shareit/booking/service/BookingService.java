package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.SearchBookingStatus;
import ru.practicum.shareit.extension.CustomPageableParameters;

import java.util.List;

public interface BookingService {
    ResponseBookingDto create(CreateBookingDto createBookingDto);

    ResponseBookingDto findOne(Long bookingId);

    ResponseBookingDto updateStatus(Long bookingId, BookingStatus newStatus);

    List<ResponseBookingDto> findAllBookedByCurrentUserByStatusOrderByDateDesc(
            SearchBookingStatus status,
            CustomPageableParameters customPageableParameters
    );

    List<ResponseBookingDto> findAllForCurrentUserItemsByStatusOrderByDateDesc(
            SearchBookingStatus status,
            CustomPageableParameters customPageableParameters
    );
}
