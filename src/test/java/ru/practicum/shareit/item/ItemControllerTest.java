package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.extension.CustomPageableParameters;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Test
    void getAllItems_whenInvoked_thenReturnedItemDtos() {
        List<ItemResponseWithBookingDto> expectedItemDtos = List.of(new ItemResponseWithBookingDto());

        when(itemService.findAll(any(CustomPageableParameters.class))).thenReturn(expectedItemDtos);

        List<ItemResponseWithBookingDto> actualItemDtos = itemController.getAllItems(null, null);
        assertThat(actualItemDtos, hasToString(expectedItemDtos.toString()));
    }

    @Test
    void findByText_whenInvoked_thenReturnedItemDto() {
        String query = "query";
        List<ItemResponseDto> expectedItemDtos = List.of(new ItemResponseDto());

        when(itemService.findByText(eq(query),any(CustomPageableParameters.class))).thenReturn(expectedItemDtos);

        List<ItemResponseDto> actualItemDtos = itemController.findByText(query,null, null);
        assertThat(actualItemDtos, hasToString(expectedItemDtos.toString()));
    }

    @Test
    void getItem_whenInvoked_thenReturnedItemDto() {
        Long itemId = 0L;
        ItemResponseWithBookingDto expectedUserDto = new ItemResponseWithBookingDto();

        when(itemService.findOne(itemId)).thenReturn(expectedUserDto);

        ItemResponseWithBookingDto actualItemDto = itemController.getItem(itemId);
        assertThat(actualItemDto, hasToString(expectedUserDto.toString()));
    }

    @Test
    void createItem_whenInvoked_thenReturnedItemDto() {
        ItemResponseDto expectedItemDto = new ItemResponseDto();
        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto();

        when(itemService.create(createItemRequestDto)).thenReturn(expectedItemDto);

        ItemResponseDto actualItemDto = itemController.createItem(createItemRequestDto);
        assertThat(actualItemDto, hasToString(expectedItemDto.toString()));
    }

    @Test
    void updateItem_whenInvoked_thenReturnedItemDto() {
        Long itemId = 0L;
        ItemResponseDto expectedItemDto = new ItemResponseDto();
        UpdateItemRequestDto updateItemRequestDto = new UpdateItemRequestDto();

        when(itemService.update(itemId, updateItemRequestDto)).thenReturn(expectedItemDto);

        ItemResponseDto actualItemDto = itemController.updateItem(itemId, updateItemRequestDto);
        assertThat(actualItemDto, hasToString(expectedItemDto.toString()));
    }

    @Test
    void deleteItem_whenInvoked_thenCheckIfDeletionWasPerformed() {
        Long itemId = 0L;
        itemController.deleteItem(itemId);
        verify(itemService).removeById(itemId);
    }

    @Test
    void addComment_whenInvoked_thenReturnedCommentDto() {
        Long itemId = 0L;
        ItemCommentResponseDto expectedItemCommentResponseDto = new ItemCommentResponseDto();
        CreateItemCommentDto createItemCommentDto = new CreateItemCommentDto();

        when(itemService.addComment(itemId, createItemCommentDto)).thenReturn(expectedItemCommentResponseDto);

        ItemCommentResponseDto actualItemCommentResponseDto = itemController.addComment(itemId, createItemCommentDto);
        assertThat(actualItemCommentResponseDto, hasToString(expectedItemCommentResponseDto.toString()));
    }
}