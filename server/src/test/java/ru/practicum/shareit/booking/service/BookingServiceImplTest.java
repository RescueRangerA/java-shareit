package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.SearchBookingStatus;
import ru.practicum.shareit.booking.exception.BookingItemThatUserOwns;
import ru.practicum.shareit.booking.exception.ImmutableBookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.extension.CustomPageableParameters;
import ru.practicum.shareit.extension.ExtendedPageRequest;
import ru.practicum.shareit.item.exception.ItemIsUnavailable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.ModelMapper;
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
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private IAuthenticationFacade authenticationFacade;

    @Mock
    private ModelMapper mapper;


    private BookingService bookingService;

    @Captor
    private ArgumentCaptor<ExtendedPageRequest> extendedPageRequestArgumentCaptor;

    private final Sort sortByStartDesc = Sort.by(new Sort.Order(Sort.Direction.DESC, "start"));

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(
                bookingRepository,
                itemRepository,
                authenticationFacade,
                mapper
        );
    }

    @Test
    void create_whenItemNotFound_thenThrownException() {
        Long itemId = 0L;

        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(
                ExtendedEntityNotFoundException.class,
                () -> bookingService.create(createBookingDto)
        );
    }

    @Test
    void create_whenItemNotAvailable_thenThrownException() {
        Long itemId = 0L;
        Item item = new Item();
        item.setAvailable(false);

        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(
                ItemIsUnavailable.class,
                () -> bookingService.create(createBookingDto)
        );
    }

    @Test
    void create_whenBookingItemThatOwn_thenThrownException() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);

        Long itemId = 0L;
        Item item = new Item();
        item.setAvailable(true);
        item.setOwner(currentUser);

        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);

        assertThrows(
                BookingItemThatUserOwns.class,
                () -> bookingService.create(createBookingDto)
        );
    }

    @Test
    void create_whenInvoked_thenReturnedBookingDto() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);

        Long itemId = 0L;
        Item item = new Item();
        item.setAvailable(true);
        item.setOwner(new User(1L, "", ""));

        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(itemId);

        Booking booking = new Booking();

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(mapper.toBooking(createBookingDto, item, currentUser)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        ResponseBookingDto actualResponseBookingDto = bookingService.create(createBookingDto);
        verify(bookingRepository).save(booking);

        assertThat(actualResponseBookingDto, equalTo(expectedResponseBookingDto));
    }

    @Test
    void findOne_whenBookingNotFound_thenThrownException() {
        Long bookingId = 0L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(
                ExtendedEntityNotFoundException.class,
                () -> bookingService.findOne(bookingId)
        );
    }

    @Test
    void findOne_whenCurrentUserEitherNotOwnerOfBookingAndItem_thenThrownException() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        User itemOwner = new User();
        itemOwner.setId(100L);
        Item item = new Item();
        item.setOwner(itemOwner);

        Long bookingId = 0L;
        User booker = new User();
        booker.setId(200L);
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);

        assertThrows(
                AccessDeniedException.class,
                () -> bookingService.findOne(bookingId)
        );
    }

    @Test
    void findOne_whenBookingFound_thenReturnedBookingDto() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        User itemOwner = new User();
        itemOwner.setId(userId);
        Item item = new Item();
        item.setOwner(itemOwner);

        Long bookingId = 0L;
        User booker = new User();
        booker.setId(200L);
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        ResponseBookingDto actualResponseBookingDto = bookingService.findOne(bookingId);

        assertThat(actualResponseBookingDto, equalTo(expectedResponseBookingDto));
    }

    @Test
    void updateStatus_whenBookingNotFound_thenThrownException() {
        Long bookingId = 0L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(
                ExtendedEntityNotFoundException.class,
                () -> bookingService.findOne(bookingId)
        );
    }

    @Test
    void updateStatus_whenCurrentUserDoesNotOwnItem_thenThrownException() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        User itemOwner = new User();
        itemOwner.setId(100L);
        Item item = new Item();
        item.setOwner(itemOwner);

        Long bookingId = 0L;
        User booker = new User();
        booker.setId(200L);
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);

        assertThrows(
                AccessDeniedException.class,
                () -> bookingService.updateStatus(bookingId, BookingStatus.APPROVED)
        );
    }

    @Test
    void updateStatus_whenBookingHasImmutableStatus_thenThrownException() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        User itemOwner = new User();
        itemOwner.setId(userId);
        Item item = new Item();
        item.setOwner(itemOwner);

        Long bookingId = 0L;
        User booker = new User();
        booker.setId(200L);
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);

        assertThrows(
                ImmutableBookingStatus.class,
                () -> bookingService.updateStatus(bookingId, BookingStatus.APPROVED)
        );
    }

    @Test
    void updateStatus_whenBookingFound_thenReturnBookingDto() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        User itemOwner = new User();
        itemOwner.setId(userId);
        Item item = new Item();
        item.setOwner(itemOwner);

        Long bookingId = 0L;
        User booker = new User();
        booker.setId(200L);
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        ResponseBookingDto actualResponseBookingDto = bookingService.updateStatus(bookingId, BookingStatus.APPROVED);

        assertThat(actualResponseBookingDto, equalTo(expectedResponseBookingDto));
    }

    @Test
    void findAllBookedByCurrentUserByStatusOrderByDateDesc_whenStatusIsAll_thenReturnedCollection() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(bookingRepository.findAllByBooker_Id(
                eq(userId),
                eq(CustomPageableParameters.of(0L,10).toPageable(sortByStartDesc)))
        ).thenReturn(bookings);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        List<ResponseBookingDto> actualBookings = bookingService.findAllBookedByCurrentUserByStatusOrderByDateDesc(
                SearchBookingStatus.ALL,
                CustomPageableParameters.of(0L,10)
        );

        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0), equalTo(expectedResponseBookingDto));
    }

    @Test
    void findAllBookedByCurrentUserByStatusOrderByDateDesc_whenStatusIsAllPaginated_thenReturnedCollection() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(bookingRepository.findAllByBooker_Id(eq(userId), ArgumentMatchers.any(ExtendedPageRequest.class))).thenReturn(bookings);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        List<ResponseBookingDto> actualBookings = bookingService.findAllBookedByCurrentUserByStatusOrderByDateDesc(
                SearchBookingStatus.ALL,
                CustomPageableParameters.of(2L, 1)
        );

        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0), equalTo(expectedResponseBookingDto));

        verify(bookingRepository).findAllByBooker_Id(eq(userId), extendedPageRequestArgumentCaptor.capture());
        assertThat(extendedPageRequestArgumentCaptor.getValue().getSort().getOrderFor("start"), equalTo(Sort.Order.desc("start")));
        assertThat(extendedPageRequestArgumentCaptor.getValue().getOffset(), equalTo(2L));
        assertThat(extendedPageRequestArgumentCaptor.getValue().getPageSize(), equalTo(1));
    }

    @Test
    void findAllBookedByCurrentUserByStatusOrderByDateDesc_whenStatusIsCurrent_thenReturnedCollection() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(bookingRepository.findByBooker_IdAndStartIsBeforeAndFinishIsAfter(
                eq(userId),
                ArgumentMatchers.any(LocalDateTime.class),
                ArgumentMatchers.any(LocalDateTime.class),
                eq(CustomPageableParameters.of(0L,10).toPageable(sortByStartDesc))
        ))
                .thenReturn(bookings);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        List<ResponseBookingDto> actualBookings = bookingService.findAllBookedByCurrentUserByStatusOrderByDateDesc(
                SearchBookingStatus.CURRENT,
                CustomPageableParameters.of(0L,10)
        );

        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0), equalTo(expectedResponseBookingDto));
    }

    @Test
    void findAllBookedByCurrentUserByStatusOrderByDateDesc_whenStatusIsPast_thenReturnedCollection() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(bookingRepository.findByBooker_IdAndFinishIsBefore(
                eq(userId),
                ArgumentMatchers.any(LocalDateTime.class),
                eq(CustomPageableParameters.of(0L,10).toPageable(sortByStartDesc))
        ))
                .thenReturn(bookings);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        List<ResponseBookingDto> actualBookings = bookingService.findAllBookedByCurrentUserByStatusOrderByDateDesc(
                SearchBookingStatus.PAST,
                CustomPageableParameters.of(0L,10)
        );

        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0), equalTo(expectedResponseBookingDto));
    }

    @Test
    void findAllBookedByCurrentUserByStatusOrderByDateDesc_whenStatusIsFuture_thenReturnedCollection() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(bookingRepository.findByBooker_IdAndStartIsAfter(
                eq(userId),
                ArgumentMatchers.any(LocalDateTime.class),
                eq(CustomPageableParameters.of(0L,10).toPageable(sortByStartDesc))
        ))
                .thenReturn(bookings);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        List<ResponseBookingDto> actualBookings = bookingService.findAllBookedByCurrentUserByStatusOrderByDateDesc(
                SearchBookingStatus.FUTURE,
                CustomPageableParameters.of(0L,10)
        );

        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0), equalTo(expectedResponseBookingDto));
    }

    @Test
    void findAllBookedByCurrentUserByStatusOrderByDateDesc_whenStatusIsWaiting_thenReturnedCollection() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(bookingRepository.findAllByBooker_IdAndStatus(
                eq(userId),
                eq(BookingStatus.WAITING),
                eq(CustomPageableParameters.of(0L,10).toPageable(sortByStartDesc))
        ))
                .thenReturn(bookings);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        List<ResponseBookingDto> actualBookings = bookingService.findAllBookedByCurrentUserByStatusOrderByDateDesc(
                SearchBookingStatus.WAITING,
                CustomPageableParameters.of(0L,10)
        );

        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0), equalTo(expectedResponseBookingDto));
    }

    @Test
    void findAllBookedByCurrentUserByStatusOrderByDateDesc_whenStatusIsRejected_thenReturnedCollection() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(bookingRepository.findAllByBooker_IdAndStatus(
                eq(userId),
                eq(BookingStatus.REJECTED),
                eq(CustomPageableParameters.of(0L,10).toPageable(sortByStartDesc))
        ))
                .thenReturn(bookings);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        List<ResponseBookingDto> actualBookings = bookingService.findAllBookedByCurrentUserByStatusOrderByDateDesc(
                SearchBookingStatus.REJECTED,
                CustomPageableParameters.of(0L,10)
        );

        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0), equalTo(expectedResponseBookingDto));
    }

    @Test
    void findAllForCurrentUserItemsByStatusOrderByDateDesc_whenStatusIsAll_thenReturnedCollection() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);

        Item item = new Item();
        List<Item> items = List.of(item);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(items);
        when(bookingRepository.findAllByItemIn(
                eq(items),
                eq(CustomPageableParameters.of(0L,10).toPageable(sortByStartDesc))
        ))
                .thenReturn(bookings);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        List<ResponseBookingDto> actualBookings = bookingService.findAllForCurrentUserItemsByStatusOrderByDateDesc(
                SearchBookingStatus.ALL,
                CustomPageableParameters.of(0L,10)
        );

        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0), equalTo(expectedResponseBookingDto));
    }

    @Test
    void findAllForCurrentUserItemsByStatusOrderByDateDesc_whenStatusIsCurrent_thenReturnedCollection() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);

        Item item = new Item();
        List<Item> items = List.of(item);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(items);
        when(bookingRepository.findByItemInAndStartIsBeforeAndFinishIsAfter(
                eq(items),
                ArgumentMatchers.any(LocalDateTime.class),
                ArgumentMatchers.any(LocalDateTime.class),
                eq(CustomPageableParameters.of(0L,10).toPageable(sortByStartDesc))
        ))
                .thenReturn(bookings);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        List<ResponseBookingDto> actualBookings = bookingService.findAllForCurrentUserItemsByStatusOrderByDateDesc(
                SearchBookingStatus.CURRENT,
                CustomPageableParameters.of(0L,10)
        );

        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0), equalTo(expectedResponseBookingDto));
    }

    @Test
    void findAllForCurrentUserItemsByStatusOrderByDateDesc_whenStatusIsPast_thenReturnedCollection() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);

        Item item = new Item();
        List<Item> items = List.of(item);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(items);
        when(bookingRepository.findByItemInAndFinishIsBefore(
                eq(items),
                ArgumentMatchers.any(LocalDateTime.class),
                eq(CustomPageableParameters.of(0L,10).toPageable(sortByStartDesc))
        ))
                .thenReturn(bookings);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        List<ResponseBookingDto> actualBookings = bookingService.findAllForCurrentUserItemsByStatusOrderByDateDesc(
                SearchBookingStatus.PAST,
                CustomPageableParameters.of(0L,10)
        );

        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0), equalTo(expectedResponseBookingDto));
    }

    @Test
    void findAllForCurrentUserItemsByStatusOrderByDateDesc_whenStatusIsFuture_thenReturnedCollection() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);

        Item item = new Item();
        List<Item> items = List.of(item);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(items);
        when(bookingRepository.findByItemInAndStartIsAfter(
                eq(items),
                ArgumentMatchers.any(LocalDateTime.class),
                eq(CustomPageableParameters.of(0L,10).toPageable(sortByStartDesc))
        ))
                .thenReturn(bookings);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        List<ResponseBookingDto> actualBookings = bookingService.findAllForCurrentUserItemsByStatusOrderByDateDesc(
                SearchBookingStatus.FUTURE,
                CustomPageableParameters.of(0L,10)
        );

        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0), equalTo(expectedResponseBookingDto));
    }

    @Test
    void findAllForCurrentUserItemsByStatusOrderByDateDesc_whenStatusIsWaiting_thenReturnedCollection() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);

        Item item = new Item();
        List<Item> items = List.of(item);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(items);
        when(bookingRepository.findAllByItemInAndStatus(
                eq(items),
                eq(BookingStatus.WAITING),
                eq(CustomPageableParameters.of(0L,10).toPageable(sortByStartDesc))
        ))
                .thenReturn(bookings);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        List<ResponseBookingDto> actualBookings = bookingService.findAllForCurrentUserItemsByStatusOrderByDateDesc(
                SearchBookingStatus.WAITING,
                CustomPageableParameters.of(0L,10)
        );

        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0), equalTo(expectedResponseBookingDto));
    }

    @Test
    void findAllForCurrentUserItemsByStatusOrderByDateDesc_whenStatusIsRejected_thenReturnedCollection() {
        Long userId = 0L;
        User currentUser = new User();
        currentUser.setId(userId);
        ExtendedUserDetails userDetails = new AuthenticatedUser(currentUser);

        Booking booking = new Booking();
        List<Booking> bookings = List.of(booking);

        Item item = new Item();
        List<Item> items = List.of(item);

        ResponseBookingDto expectedResponseBookingDto = new ResponseBookingDto();

        when(authenticationFacade.getCurrentUserDetails()).thenReturn(userDetails);
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(items);
        when(bookingRepository.findAllByItemInAndStatus(
                eq(items),
                eq(BookingStatus.REJECTED),
                eq(CustomPageableParameters.of(0L,10).toPageable(sortByStartDesc))
        ))
                .thenReturn(bookings);
        when(mapper.toResponseBookingDto(booking)).thenReturn(expectedResponseBookingDto);

        List<ResponseBookingDto> actualBookings = bookingService.findAllForCurrentUserItemsByStatusOrderByDateDesc(
                SearchBookingStatus.REJECTED,
                CustomPageableParameters.of(0L,10)
        );

        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0), equalTo(expectedResponseBookingDto));
    }
}