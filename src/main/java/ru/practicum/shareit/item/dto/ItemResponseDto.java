package ru.practicum.shareit.item.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemResponseDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long ownerId;

    private Set<ItemCommentResponseDto> comments;

    private Long requestId;
}
