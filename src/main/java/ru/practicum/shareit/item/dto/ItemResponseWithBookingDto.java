package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.ShortResponseBookingForItemDto;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemResponseWithBookingDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long ownerId;

    private ShortResponseBookingForItemDto lastBooking;

    private ShortResponseBookingForItemDto nextBooking;

    private Set<ItemCommentResponseDto> comments;
}
