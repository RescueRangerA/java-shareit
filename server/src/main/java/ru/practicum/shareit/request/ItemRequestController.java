package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.extension.CustomPageableParameters;
import ru.practicum.shareit.request.dto.CreateItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsResponseDto findById(
            @PathVariable Long requestId
    ) {
        return itemRequestService.findById(requestId);
    }

    @GetMapping
    public List<ItemRequestWithItemsResponseDto> findAllForCurrentUser() {
        return itemRequestService.findAllForCurrentUser();
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemsResponseDto> findAllForOtherUsers(
            @RequestParam(required = false, defaultValue = "0") Long from,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return itemRequestService.findAllCreatedByOthers(CustomPageableParameters.of(from, size));
    }

    @PostMapping
    public ItemRequestResponseDto create(
            @RequestBody CreateItemRequestRequestDto createItemRequestRequestDto
    ) {
        return itemRequestService.create(createItemRequestRequestDto);
    }
}
