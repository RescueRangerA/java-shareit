package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.extension.CustomPageableParameters;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.request.dto.CreateItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.security.facade.IAuthenticationFacade;
import ru.practicum.shareit.security.user.AuthenticatedUser;
import ru.practicum.shareit.security.user.ExtendedUserDetails;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private IAuthenticationFacade authenticationFacade;

    private ItemRequestService itemRequestService;

    private final Sort sortByCreatedDesc = Sort.by(new Sort.Order(Sort.Direction.DESC, "created"));

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                modelMapper,
                authenticationFacade
        );
    }

    @Test
    void findById_whenItemNotFound_thenThrowException() {
        Long itemId = 0L;

        when(itemRequestRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ExtendedEntityNotFoundException.class, () -> itemRequestService.findById(itemId));
    }

    @Test
    void findById_whenItemFound_thenReturnedItemRequestDto() {
        Long itemRequestId = 0L;
        ItemRequest itemRequest = new ItemRequest();
        ItemRequestWithItemsResponseDto expectedItemRequestWithItemsResponseDto = new ItemRequestWithItemsResponseDto();

        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));
        when(modelMapper.toItemRequestWithItemsResponseDto(itemRequest)).thenReturn(expectedItemRequestWithItemsResponseDto);

        ItemRequestWithItemsResponseDto actualItem = itemRequestService.findById(itemRequestId);

        assertThat(actualItem, equalTo(expectedItemRequestWithItemsResponseDto));
    }

    @Test
    void findAllForCurrentUser() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);
        ItemRequest itemRequest = new ItemRequest();
        List<ItemRequest> itemRequestList = List.of(itemRequest);
        ItemRequestWithItemsResponseDto expectedItemRequestWithItemsResponseDto = new ItemRequestWithItemsResponseDto();


        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(itemRequestRepository.findAllByRequestor_Id(userId)).thenReturn(itemRequestList);
        when(modelMapper.toItemRequestWithItemsResponseDto(itemRequest)).thenReturn(expectedItemRequestWithItemsResponseDto);

        List<ItemRequestWithItemsResponseDto> actualItems = itemRequestService.findAllForCurrentUser();

        assertThat(actualItems.size(), equalTo(1));
        assertThat(actualItems.get(0), equalTo(expectedItemRequestWithItemsResponseDto));
    }

    @Test
    void findAllCreatedByOthers() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);
        ItemRequest itemRequest = new ItemRequest();
        List<ItemRequest> itemRequestList = List.of(itemRequest);
        ItemRequestWithItemsResponseDto expectedItemRequestWithItemsResponseDto = new ItemRequestWithItemsResponseDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(itemRequestRepository.findAllByRequestor_IdNot(
                        eq(userId),
                        eq(CustomPageableParameters.of(0L, 10).toPageable(sortByCreatedDesc))
                )
        ).thenReturn(itemRequestList);
        when(modelMapper.toItemRequestWithItemsResponseDto(itemRequest)).thenReturn(expectedItemRequestWithItemsResponseDto);

        List<ItemRequestWithItemsResponseDto> actualItems = itemRequestService.findAllCreatedByOthers(CustomPageableParameters.of(0L, 10));

        assertThat(actualItems.size(), equalTo(1));
        assertThat(actualItems.get(0), equalTo(expectedItemRequestWithItemsResponseDto));
    }

    @Test
    void create() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);

        CreateItemRequestRequestDto createItemRequestRequestDto = new CreateItemRequestRequestDto();
        ItemRequest itemRequest = new ItemRequest();
        ItemRequestResponseDto expectedItemRequestResponseDto = new ItemRequestResponseDto();

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(modelMapper.toItemRequest(createItemRequestRequestDto, currentUser)).thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(modelMapper.toItemRequestResponseDto(itemRequest)).thenReturn(expectedItemRequestResponseDto);

        ItemRequestResponseDto actualItemRequestResponseDto = itemRequestService.create(createItemRequestRequestDto);

        verify(itemRequestRepository).save(itemRequest);
        assertThat(actualItemRequestResponseDto, equalTo(expectedItemRequestResponseDto));
    }
}