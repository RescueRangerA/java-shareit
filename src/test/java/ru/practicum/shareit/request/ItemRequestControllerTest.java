package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.extension.CustomPageableParameters;
import ru.practicum.shareit.request.dto.CreateItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Test
    void findById_whenInvoked_thenReturnedItemRequestDto() {
        Long itemRequestId = 0L;
        ItemRequestWithItemsResponseDto expectedItemRequestWithItemsResponseDto = new ItemRequestWithItemsResponseDto();

        when(itemRequestService.findById(itemRequestId)).thenReturn(expectedItemRequestWithItemsResponseDto);

        ItemRequestWithItemsResponseDto actualItemRequestWithItemsResponseDto = itemRequestController.findById(itemRequestId);
        assertThat(actualItemRequestWithItemsResponseDto, hasToString(expectedItemRequestWithItemsResponseDto.toString()));
    }

    @Test
    void findAllForCurrentUser_whenInvoked_thenReturnedItemRequestCollection() {
        List<ItemRequestWithItemsResponseDto> expectedItemRequestWithItemsResponseDtos = List.of(new ItemRequestWithItemsResponseDto());

        when(
                itemRequestService.findAllForCurrentUser()
        )
                .thenReturn(expectedItemRequestWithItemsResponseDtos);

        List<ItemRequestWithItemsResponseDto> actualItemRequestWithItemsResponseDtos = itemRequestController.findAllForCurrentUser();
        assertThat(actualItemRequestWithItemsResponseDtos, hasToString(expectedItemRequestWithItemsResponseDtos.toString()));
    }

    @Test
    void findAllForOtherUsers_whenInvoked_thenReturnedItemRequestCollection() {
        List<ItemRequestWithItemsResponseDto> expectedItemRequestWithItemsResponseDtos = List.of(new ItemRequestWithItemsResponseDto());

        when(
                itemRequestService.findAllCreatedByOthers(any(CustomPageableParameters.class))
        )
                .thenReturn(expectedItemRequestWithItemsResponseDtos);

        List<ItemRequestWithItemsResponseDto> actualItemRequestWithItemsResponseDtos = itemRequestController.findAllForOtherUsers(null, null);
        assertThat(actualItemRequestWithItemsResponseDtos, hasToString(expectedItemRequestWithItemsResponseDtos.toString()));
    }

    @Test
    void create_whenInvoked_thenReturnedItemRequestDto() {
        CreateItemRequestRequestDto createItemRequestRequestDto = new CreateItemRequestRequestDto();
        ItemRequestResponseDto expectedItemRequestResponseDto = new ItemRequestResponseDto();

        when(itemRequestService.create(createItemRequestRequestDto)).thenReturn(expectedItemRequestResponseDto);

        ItemRequestResponseDto actualItemRequestResponseDto = itemRequestController.create(createItemRequestRequestDto);
        assertThat(actualItemRequestResponseDto, hasToString(expectedItemRequestResponseDto.toString()));
    }
}