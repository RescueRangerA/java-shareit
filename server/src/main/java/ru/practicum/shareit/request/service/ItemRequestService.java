package ru.practicum.shareit.request.service;

import ru.practicum.shareit.extension.CustomPageableParameters;
import ru.practicum.shareit.request.dto.CreateItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestWithItemsResponseDto findById(Long itemRequestId);

    List<ItemRequestWithItemsResponseDto> findAllForCurrentUser();

    List<ItemRequestWithItemsResponseDto> findAllCreatedByOthers(CustomPageableParameters customPageableParameters);

    ItemRequestResponseDto create(CreateItemRequestRequestDto createItemRequestRequestDto);
}
