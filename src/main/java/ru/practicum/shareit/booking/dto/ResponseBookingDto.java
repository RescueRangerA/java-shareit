package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseBookingDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;

    private UserResponseDto booker;

    private ItemResponseDto item;
}
