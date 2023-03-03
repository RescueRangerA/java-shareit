package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.extension.CustomPageableParameters;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponseWithBookingDto> getAllItems(
            @RequestParam(required = false, defaultValue = "0") Long from,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return itemService.findAll(CustomPageableParameters.of(from, size));
    }

    @GetMapping("/search")
    public List<ItemResponseDto> findByText(
            @RequestParam String text,
            @RequestParam(required = false, defaultValue = "0") Long from,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return itemService.findByText(text, CustomPageableParameters.of(from, size));
    }

    @GetMapping("/{itemId}")
    public ItemResponseWithBookingDto getItem(
            @PathVariable Long itemId
    ) {
        return itemService.findOne(itemId);
    }

    @PostMapping
    public ItemResponseDto createItem(
            @RequestBody CreateItemRequestDto createItemRequestDto
    ) {
        return itemService.create(createItemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(
            @PathVariable Long itemId,
            @RequestBody UpdateItemRequestDto updateItemRequestDto
    ) {
        return itemService.update(itemId, updateItemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @PathVariable Long itemId
    ) {
        itemService.removeById(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ItemCommentResponseDto addComment(
            @PathVariable Long itemId,
            @RequestBody CreateItemCommentDto createItemCommentDto
    ) {
        return itemService.addComment(itemId, createItemCommentDto);
    }
}
