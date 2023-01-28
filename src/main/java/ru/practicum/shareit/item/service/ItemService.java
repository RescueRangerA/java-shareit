package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import javax.security.sasl.AuthenticationException;
import java.util.List;

public interface ItemService {
    List<ItemResponseDto> findAll();

    List<ItemResponseDto> findByText(String text);

    ItemResponseDto findOne(Long itemId) throws AuthenticationException;

    ItemResponseDto create(CreateItemRequestDto createItemRequestDto);

    ItemResponseDto update(Long itemId, UpdateItemRequestDto updateItemRequestDto) throws AuthenticationException;

    void removeById(Long itemId) throws AuthenticationException;
}
