package ru.practicum.shareIt.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareIt.request.dto.CreateItemRequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    private static final String HEADER_NAME = "X-Sharer-User-Id";

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(
            @RequestHeader(HEADER_NAME) Long userId,
            @PathVariable @Positive Long requestId
    ) {
        log.info("Get item request {}, userId={}", requestId, userId);

        return itemRequestClient.findById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllForCurrentUser(
            @RequestHeader(HEADER_NAME) Long userId
    ) {
        log.info("Get all item requests, userId={}", userId);

        return itemRequestClient.findAllForCurrentUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllForOtherUsers(
            @RequestHeader(HEADER_NAME) Long userId,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size
    ) {
        log.info("Get item requests for other users userId={}, from={}, size={}", userId, from, size);

        return itemRequestClient.findAllCreatedByOthers(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(HEADER_NAME) Long userId,
            @RequestBody @Valid CreateItemRequestRequestDto createItemRequestRequestDto
    ) {
        log.info("Creating item request {}, userId={}", createItemRequestRequestDto, userId);

        return itemRequestClient.create(userId, createItemRequestRequestDto);
    }
}
