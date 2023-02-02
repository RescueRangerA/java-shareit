package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponseWithBookingDto> getAllItems() {
        return itemService.findAll();
    }

    @GetMapping("/search")
    public List<ItemResponseDto> findByText(@RequestParam @NotBlank String text) {
        return itemService.findByText(text);
    }

    @GetMapping("/{itemId}")
    public ItemResponseWithBookingDto getItem(@PathVariable @Positive Long itemId) {
        return itemService.findOne(itemId);
    }

    @PostMapping
    public ItemResponseDto createItem(@Valid @RequestBody CreateItemRequestDto createItemRequestDto) {
        return itemService.create(createItemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@PathVariable @Positive Long itemId, @Valid @RequestBody UpdateItemRequestDto updateItemRequestDto) {
        return itemService.update(itemId, updateItemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable @Positive Long itemId) {
        itemService.removeById(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ItemCommentResponseDto addComment(@PathVariable @Positive Long itemId, @Valid @RequestBody CreateItemCommentDto createItemCommentDto) {
        return itemService.addComment(itemId, createItemCommentDto);
    }
}
