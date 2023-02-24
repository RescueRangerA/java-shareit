package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShortResponseBookingForItemDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long bookerId;
}
