package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateItemRequestDto {

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
