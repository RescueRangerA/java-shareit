package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemRequestWithItemsResponseDto {
    private Long id;

    private String description;

    private LocalDateTime created;

    private Set<ItemResponseDto> items;
}
