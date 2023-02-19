package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.extension.CustomPageableParameters;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponseWithBookingDto> getAllItems(
            @RequestParam(required = false) @PositiveOrZero Long from,
            @RequestParam(required = false) @Positive Integer size
    ) {
        return itemService.findAll(CustomPageableParameters.of(from, size));
    }

    @GetMapping("/search")
    public List<ItemResponseDto> findByText(
            @RequestParam String text,
            @RequestParam(required = false) @PositiveOrZero Long from,
            @RequestParam(required = false) @Positive Integer size
    ) {
        return itemService.findByText(text, CustomPageableParameters.of(from, size));
    }

    @GetMapping("/{itemId}")
    public ItemResponseWithBookingDto getItem(
            @PathVariable @Positive Long itemId
    ) {
        return itemService.findOne(itemId);
    }

    @PostMapping
    public ItemResponseDto createItem(
            @RequestBody @Valid CreateItemRequestDto createItemRequestDto
    ) {
        return itemService.create(createItemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(
            @PathVariable @Positive Long itemId,
            @RequestBody @Valid UpdateItemRequestDto updateItemRequestDto
    ) {
        return itemService.update(itemId, updateItemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @PathVariable @Positive Long itemId
    ) {
        itemService.removeById(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ItemCommentResponseDto addComment(
            @PathVariable @Positive Long itemId,
            @RequestBody @Valid CreateItemCommentDto createItemCommentDto
    ) {
        return itemService.addComment(itemId, createItemCommentDto);
    }
}
