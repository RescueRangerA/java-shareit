package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public interface ItemMapper {
    ItemResponseDto toItemResponseDto(Item item);

    Item toItem(Long itemId, UpdateItemRequestDto itemRequestDto, User user);

    Item toItem(CreateItemRequestDto itemRequestDto, User user);
}
