package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    List<ItemResponseWithBookingDto> findAll();

    List<ItemResponseDto> findByText(String text);

    ItemResponseWithBookingDto findOne(Long itemId);

    ItemResponseDto create(CreateItemRequestDto createItemRequestDto);

    ItemResponseDto update(Long itemId, UpdateItemRequestDto updateItemRequestDto);

    ItemCommentResponseDto addComment(Long itemId, CreateItemCommentDto createItemCommentDto);

    void removeById(Long itemId);
}
