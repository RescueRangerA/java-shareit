package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.extension.CustomPageableParameters;
import ru.practicum.shareit.extension.ExtendedPageRequest;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exception.NotAllowedToAddComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemCommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.ItemWithBookingProjection;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.security.facade.IAuthenticationFacade;
import ru.practicum.shareit.security.user.AuthenticatedUser;
import ru.practicum.shareit.security.user.ExtendedUserDetails;
import ru.practicum.shareit.user.model.User;


import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    static class ItemWithBookingProjectionImpl implements ItemWithBookingProjection {
        private final Item item;

        private final Booking lastBooking;

        private final Booking nextBooking;

        public ItemWithBookingProjectionImpl(Item item, Booking lastBooking, Booking nextBooking) {
            this.item = item;
            this.lastBooking = lastBooking;
            this.nextBooking = nextBooking;
        }

        @Override
        public Item getItem() {
            return item;
        }

        @Override
        public Booking getLastBooking() {
            return lastBooking;
        }

        @Override
        public Booking getNextBooking() {
            return nextBooking;
        }
    }

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemCommentRepository itemCommentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private IAuthenticationFacade authenticationFacade;

    private ItemService itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(
                itemRepository,
                bookingRepository,
                itemCommentRepository,
                itemRequestRepository,
                modelMapper,
                authenticationFacade
        );
    }

    @Test
    void findAll_whenInvoked_thenItemsCollectionInResult() {
        User currentUser = new User();
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);
        ItemWithBookingProjection projection = new ItemWithBookingProjectionImpl(new Item(), new Booking(), new Booking());
        ItemResponseWithBookingDto expectedItemResponseWithBookingDto = new ItemResponseWithBookingDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);

        when(
                itemRepository.findAllAvailableTrueByOwner_IdWithClosestBookings(
                        eq(currentUser.getId()),
                        ArgumentMatchers.any(LocalDateTime.class),
                        eq(ExtendedPageRequest.ofOffset(0L, 10))
                )
        )
                .thenReturn(List.of(projection));

        when(
                modelMapper.toItemResponseWithBookingDto(
                        projection.getItem(),
                        Optional.ofNullable(projection.getLastBooking()),
                        Optional.ofNullable(projection.getNextBooking()))
        )
                .thenReturn(expectedItemResponseWithBookingDto);

        List<ItemResponseWithBookingDto> actualItems = itemService.findAll(CustomPageableParameters.of(0L, 10));
        assertThat(actualItems.size(), equalTo(1));
        assertThat(actualItems.get(0), equalTo(expectedItemResponseWithBookingDto));
    }

    @Test
    void findByText_whenQueryIsEmptyString_thenReturnedEmptyCollectionImmediately() {
        List<ItemResponseDto> items = itemService.findByText("", CustomPageableParameters.of(0L, 10));
        assertThat(items.size(), equalTo(0));

        verifyNoInteractions(itemRepository);
    }

    @Test
    void findByText_whenItemFound_thenItemsCollectionInResult() {
        Item item = new Item();
        ItemResponseDto expectedItemResponseDto = new ItemResponseDto();
        String query = "name";

        when(itemRepository.findAllAvailableTrueAndNameOrDescriptionLikeIgnoreCase(
                query,
                ExtendedPageRequest.ofOffset(0L, 10))
        )
                .thenReturn(List.of(item));

        when(modelMapper.toItemResponseDto(item)).thenReturn(expectedItemResponseDto);

        List<ItemResponseDto> actualItems = itemService.findByText(query, CustomPageableParameters.of(0L, 10));
        assertThat(actualItems.size(), equalTo(1));
        assertThat(actualItems.get(0), equalTo(expectedItemResponseDto));
    }

    @Test
    void findOne_whenItemNotFound_thenThrowException() {
        Long itemId = 0L;

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ExtendedEntityNotFoundException.class, () -> itemService.findOne(itemId));
    }

    @Test
    void findOne_whenItemFoundByNonOwner_thenReturnedItemDto() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Long itemId = 0L;
        Long ownerId = 1L;
        Item item = new Item();
        item.setOwner(new User(ownerId, "", ""));
        ItemResponseWithBookingDto expectedItemResponseWithBookingDto = new ItemResponseWithBookingDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(modelMapper.toItemResponseWithBookingDto(item, Optional.empty(), Optional.empty())).thenReturn(expectedItemResponseWithBookingDto);

        ItemResponseWithBookingDto actualItem = itemService.findOne(itemId);
        verifyNoInteractions(bookingRepository);

        assertThat(actualItem, equalTo(expectedItemResponseWithBookingDto));
    }

    @Test
    void findOne_whenItemFoundByOwner_thenReturnedItemWithBookingDto() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Long itemId = 0L;
        Item item = new Item();
        item.setOwner(currentUser);
        Booking lastBooking = new Booking();
        Booking nextBooking = new Booking();
        ItemResponseWithBookingDto expectedItemResponseWithBookingDto = new ItemResponseWithBookingDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findLastByItemAndStartIsBefore(eq(item), ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemAndFinishIsAfter(eq(item), ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(Optional.of(nextBooking));
        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);

        when(modelMapper.toItemResponseWithBookingDto(item, Optional.of(lastBooking), Optional.of(nextBooking)))
                .thenReturn(expectedItemResponseWithBookingDto);

        ItemResponseWithBookingDto actualItem = itemService.findOne(itemId);
        verify(bookingRepository).findLastByItemAndStartIsBefore(eq(item), ArgumentMatchers.any(LocalDateTime.class));
        verify(bookingRepository).findFirstByItemAndFinishIsAfter(eq(item), ArgumentMatchers.any(LocalDateTime.class));
        assertThat(actualItem, equalTo(expectedItemResponseWithBookingDto));
    }

    @Test
    void create_whenInvoked_thenReturnedItemDto() {
        User currentUser = new User();
        Item itemToSave = new Item();
        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto();
        ItemResponseDto expectedItemDto = new ItemResponseDto();

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(modelMapper.toItem(createItemRequestDto, currentUser, Optional.empty())).thenReturn(itemToSave);
        when(itemRepository.save(itemToSave)).thenReturn(itemToSave);
        when(modelMapper.toItemResponseDto(itemToSave)).thenReturn(expectedItemDto);

        ItemResponseDto actualItemDto = itemService.create(createItemRequestDto);

        verify(itemRepository).save(itemToSave);
        assertThat(actualItemDto, equalTo(expectedItemDto));
    }

    @Test
    void update_whenItemNotFound_thenThrownException() {
        Long itemId = 0L;
        UpdateItemRequestDto updateItemRequestDto = new UpdateItemRequestDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(
                ExtendedEntityNotFoundException.class,
                () -> itemService.update(
                        itemId,
                        updateItemRequestDto
                )
        );
    }

    @Test
    void update_whenDoNotOwnItem_thenThrownException() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Long ownerUserId = 1L;
        User itemOwner = new User();
        itemOwner.setId(ownerUserId);

        Long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(itemOwner);
        UpdateItemRequestDto updateItemRequestDto = new UpdateItemRequestDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);

        assertThrows(
                AccessDeniedException.class,
                () -> itemService.update(
                        itemId,
                        updateItemRequestDto
                )
        );
    }

    @Test
    void update_whenItemFound_thenUpdateFields() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Long itemId = 0L;
        Item oldItem = new Item();
        oldItem.setId(itemId);
        oldItem.setName("item");
        oldItem.setDescription("desc");
        oldItem.setAvailable(true);
        oldItem.setOwner(currentUser);

        UpdateItemRequestDto updateItemRequestDto = new UpdateItemRequestDto();
        updateItemRequestDto.setName("item2");
        updateItemRequestDto.setDescription("desc2");
        updateItemRequestDto.setAvailable(false);

        ItemResponseDto expectedItemResponseDto = new ItemResponseDto();
        expectedItemResponseDto.setId(itemId);
        expectedItemResponseDto.setName("item2");
        expectedItemResponseDto.setDescription("desc2");
        expectedItemResponseDto.setAvailable(false);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));
        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(modelMapper.toItemResponseDto(oldItem)).thenReturn(expectedItemResponseDto);

        ItemResponseDto actualItemResponseDto = itemService.update(
                itemId,
                updateItemRequestDto
        );

        verify(modelMapper).toItemResponseDto(itemArgumentCaptor.capture());
        Item updatedOldItem = itemArgumentCaptor.getValue();
        assertThat(updatedOldItem.getId(), equalTo(oldItem.getId()));
        assertThat(updatedOldItem.getName(), equalTo(updateItemRequestDto.getName()));
        assertThat(updatedOldItem.getDescription(), equalTo(updateItemRequestDto.getDescription()));
        assertThat(updatedOldItem.getAvailable(), equalTo(updateItemRequestDto.getAvailable()));

        assertThat(actualItemResponseDto, equalTo(expectedItemResponseDto));
    }

    @Test
    void addComment_whenItemNotFound_thenThrownException() {
        Long itemId = 0L;
        CreateItemCommentDto createItemCommentDto = new CreateItemCommentDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(
                ExtendedEntityNotFoundException.class,
                () -> itemService.addComment(
                        itemId,
                        createItemCommentDto
                )
        );
    }

    @Test
    void addComment_whenCurrentUserHaveNotEverFinishedBookingWithTheItem_thenThrownException() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);

        Long itemId = 0L;
        Item item = new Item();
        CreateItemCommentDto createItemCommentDto = new CreateItemCommentDto();

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(
                bookingRepository.existsBookingByBookerAndItemAndFinishIsBeforeAndStatus(
                        eq(currentUser),
                        eq(item),
                        ArgumentMatchers.any(LocalDateTime.class),
                        eq(BookingStatus.APPROVED)
                )
        )
                .thenReturn(false);

        assertThrows(
                NotAllowedToAddComments.class,
                () -> itemService.addComment(
                        itemId,
                        createItemCommentDto
                )
        );
    }

    @Test
    void addComment_whenItemFoundAndCanAddComment_thenReturnedItemCommentDto() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);

        Long itemId = 0L;
        Item item = new Item();
        CreateItemCommentDto createItemCommentDto = new CreateItemCommentDto();

        Comment comment = new Comment();
        ItemCommentResponseDto expectedItemCommentResponseDto = new ItemCommentResponseDto();

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(
                bookingRepository.existsBookingByBookerAndItemAndFinishIsBeforeAndStatus(
                        eq(currentUser),
                        eq(item),
                        ArgumentMatchers.any(LocalDateTime.class),
                        eq(BookingStatus.APPROVED)
                )
        )
                .thenReturn(true);
        when(modelMapper.toItemComment(createItemCommentDto, currentUser, item)).thenReturn(comment);
        when(itemCommentRepository.save(comment)).thenReturn(comment);
        when(modelMapper.toItemCommentResponseDto(comment)).thenReturn(expectedItemCommentResponseDto);


        ItemCommentResponseDto actualItemCommentResponseDto = itemService.addComment(
                itemId,
                createItemCommentDto
        );
        verify(itemCommentRepository).save(comment);

        assertThat(actualItemCommentResponseDto, equalTo(expectedItemCommentResponseDto));
    }


    @Test
    void removeById_whenItemFound_thenCheckIfDeleteByIdWasCalled() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(currentUser);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);

        itemService.removeById(itemId);

        verify(itemRepository).deleteById(itemId);
    }

    @Test
    void removeById_whenItemNotFound_thenThrownException() {
        Long itemId = 0L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(
                ExtendedEntityNotFoundException.class,
                () -> itemService.removeById(itemId)
        );
    }

    @Test
    void removeById_whenDoNotOwnItem_thenThrownException() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Long ownerUserId = 1L;
        User itemOwner = new User();
        itemOwner.setId(ownerUserId);

        Long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(itemOwner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);

        assertThrows(
                AccessDeniedException.class,
                () -> itemService.removeById(itemId)
        );
    }
}