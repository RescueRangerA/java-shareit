package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.security.sasl.AuthenticationException;
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
    public List<ItemResponseDto> getAllItems() {
        return itemService.findAll();
    }

    @GetMapping("/search")
    public List<ItemResponseDto> findByText(@RequestParam @NotBlank String text) {
        return itemService.findByText(text);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@PathVariable @Positive Long itemId) throws AuthenticationException {
        return itemService.findOne(itemId);
    }

    @PostMapping
    public ItemResponseDto createItem(@Valid @RequestBody CreateItemRequestDto createItemRequestDto) {
        return itemService.create(createItemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@PathVariable @Positive Long itemId, @Valid @RequestBody UpdateItemRequestDto updateItemRequestDto) throws AuthenticationException {
        return itemService.update(itemId, updateItemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable @Positive Long itemId) throws AuthenticationException {
        itemService.removeById(itemId);
    }
}
