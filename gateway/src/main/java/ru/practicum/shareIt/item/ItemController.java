package ru.practicum.shareIt.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareIt.item.dto.CreateItemCommentDto;
import ru.practicum.shareIt.item.dto.CreateItemRequestDto;
import ru.practicum.shareIt.item.dto.UpdateItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    private static final String HEADER_NAME = "X-Sharer-User-Id";

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(
            @RequestHeader(HEADER_NAME) Long userId,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size
    ) {
        log.info("Get all items userId={}, from={}, size={}", userId, from, size);

        return itemClient.findAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByText(
            @RequestHeader(HEADER_NAME) Long userId,
            @RequestParam String text,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size
    ) {
        log.info("Find item by text '{}', userId={}, from={}, size={}", text, userId, from, size);

        return itemClient.findByText(userId, text, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader(HEADER_NAME) Long userId,
            @PathVariable @Positive Long itemId
    ) {
        log.info("Get item {}, userId={}", itemId, userId);

        return itemClient.findOne(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader(HEADER_NAME) Long userId,
            @RequestBody @Valid CreateItemRequestDto createItemRequestDto
    ) {
        log.info("Creating item {}, userId={}", createItemRequestDto, userId);

        return itemClient.create(userId, createItemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(HEADER_NAME) Long userId,
            @PathVariable @Positive Long itemId,
            @RequestBody @Valid UpdateItemRequestDto updateItemRequestDto
    ) {
        log.info("Updating item with id {}, {}, userId={}", itemId, updateItemRequestDto, userId);

        return itemClient.update(userId, itemId, updateItemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @RequestHeader(HEADER_NAME) Long userId,
            @PathVariable @Positive Long itemId
    ) {
        log.info("Deleting item {}, userId={}", itemId, itemId);

        return itemClient.removeById(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(HEADER_NAME) Long userId,
            @PathVariable @Positive Long itemId,
            @RequestBody @Valid CreateItemCommentDto createItemCommentDto
    ) {
        log.info("Adding comment {} for item id {}, userId={}", createItemCommentDto, itemId, itemId);

        return itemClient.addComment(userId, itemId, createItemCommentDto);
    }
}
