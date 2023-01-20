package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class ItemMapperImpl implements ItemMapper {

    @Override
    public ItemResponseDto toItemResponseDto(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    @Override
    public Item toItem(Long itemId, UpdateItemRequestDto itemRequestDto, User user) {
        return new Item(
                itemId,
                itemRequestDto.getName(),
                itemRequestDto.getDescription(),
                itemRequestDto.getAvailable(),
                user
        );
    }

    @Override
    public Item toItem(CreateItemRequestDto itemRequestDto, User user) {
        return new Item(
                null,
                itemRequestDto.getName(),
                itemRequestDto.getDescription(),
                itemRequestDto.getAvailable(),
                user
        );
    }
}
