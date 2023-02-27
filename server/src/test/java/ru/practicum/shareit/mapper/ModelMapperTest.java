package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.ShortResponseBookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelMapperTest {

    @Spy
    private ModelMapper mapper;

    @Test
    void toBooking() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime finish = LocalDateTime.now().plusDays(1);

        CreateBookingDto createBookingDto = new CreateBookingDto(
                start,
                finish,
                0L
        );
        Item item = new Item();
        User user = new User();

        Booking booking = mapper.toBooking(createBookingDto, item, user);
        assertThat(booking.getId(), equalTo(0L));
        assertThat(booking.getStart(), equalTo(start));
        assertThat(booking.getFinish(), equalTo(finish));
        assertThat(booking.getItem(), equalTo(item));
        assertThat(booking.getBooker(), equalTo(user));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void toResponseBookingDto() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime finish = LocalDateTime.now().plusDays(1);

        User user = new User();
        Item item = new Item();

        Booking booking = new Booking(
                0L,
                start,
                finish,
                item,
                user,
                BookingStatus.WAITING
        );

        UserResponseDto userResponseDto = new UserResponseDto();
        doReturn(userResponseDto).when(mapper).toUserResponseDto(user);
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        doReturn(itemResponseDto).when(mapper).toItemResponseDto(item);

        ResponseBookingDto bookingDto = mapper.toResponseBookingDto(booking);

        assertThat(bookingDto.getId(), equalTo(0L));
        assertThat(bookingDto.getStart(), equalTo(start));
        assertThat(bookingDto.getEnd(), equalTo(finish));
        assertThat(bookingDto.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(bookingDto.getBooker(), equalTo(userResponseDto));
        assertThat(bookingDto.getItem(), equalTo(itemResponseDto));
    }

    @Test
    void toResponseShortBookingDto() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime finish = LocalDateTime.now().plusDays(1);

        Booking booking = new Booking(
                0L,
                start,
                finish,
                new Item(),
                new User(0L, "", ""),
                BookingStatus.WAITING
        );

        ShortResponseBookingForItemDto shortBookingDto = mapper.toResponseShortBookingDto(booking);
        assertThat(shortBookingDto.getId(), equalTo(0L));
        assertThat(shortBookingDto.getStart(), equalTo(start));
        assertThat(shortBookingDto.getEnd(), equalTo(finish));
        assertThat(shortBookingDto.getBookerId(), equalTo(0L));
    }

    @Test
    void toItemResponseDto() {
        User user = new User(0L, "", "");
        Comment comment = new Comment();
        Item item = new Item(0L, "name", "desc", true, user, Set.of(comment), null);
        ItemCommentResponseDto itemCommentResponseDto = new ItemCommentResponseDto();

        doReturn(itemCommentResponseDto).when(mapper).toItemCommentResponseDto(comment);

        ItemResponseDto itemResponseDto = mapper.toItemResponseDto(item);
        assertThat(itemResponseDto.getId(), equalTo(0L));
        assertThat(itemResponseDto.getName(), equalTo("name"));
        assertThat(itemResponseDto.getDescription(), equalTo("desc"));
        assertThat(itemResponseDto.getAvailable(), equalTo(true));
        assertThat(itemResponseDto.getOwnerId(), equalTo(user.getId()));
        assertThat(itemResponseDto.getComments().size(), equalTo(1));
        assertThat(itemResponseDto.getComments().contains(itemCommentResponseDto), equalTo(true));
    }

    @Test
    void toItemResponseWithBookingDto() {
        User user = new User(0L, "", "");
        Comment comment = new Comment();
        Item item = new Item(0L, "name", "desc", true, user, Set.of(comment), null);
        ItemCommentResponseDto itemCommentResponseDto = new ItemCommentResponseDto();
        Booking lastBooking = new Booking();
        Booking nextBooking = new Booking();

        ShortResponseBookingForItemDto lastBookingDto = new ShortResponseBookingForItemDto();
        ShortResponseBookingForItemDto nextBookingDto = new ShortResponseBookingForItemDto();
        doReturn(itemCommentResponseDto).when(mapper).toItemCommentResponseDto(comment);
        doReturn(lastBookingDto).when(mapper).toResponseShortBookingDto(lastBooking);
        doReturn(nextBookingDto).when(mapper).toResponseShortBookingDto(nextBooking);

        ItemResponseWithBookingDto itemResponseWithBookingDto = mapper.toItemResponseWithBookingDto(
                item,
                Optional.of(lastBooking),
                Optional.of(nextBooking)
        );

        assertThat(itemResponseWithBookingDto.getId(), equalTo(0L));
        assertThat(itemResponseWithBookingDto.getName(), equalTo("name"));
        assertThat(itemResponseWithBookingDto.getDescription(), equalTo("desc"));
        assertThat(itemResponseWithBookingDto.getAvailable(), equalTo(true));
        assertThat(itemResponseWithBookingDto.getOwnerId(), equalTo(user.getId()));
        assertThat(itemResponseWithBookingDto.getLastBooking(), equalTo(lastBookingDto));
        assertThat(itemResponseWithBookingDto.getNextBooking(), equalTo(nextBookingDto));
        assertThat(itemResponseWithBookingDto.getComments().size(), equalTo(1));
        assertThat(itemResponseWithBookingDto.getComments().contains(itemCommentResponseDto), equalTo(true));
    }

    @Test
    void toItem() {
        User user = new User(0L, "", "");
        User requestor = new User(1L, "", "");
        ItemRequest itemRequest = new ItemRequest(0L, "", requestor, LocalDateTime.now(), Collections.emptySet());
        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto(
                "name", "desc", true, 0L
        );

        Item item = mapper.toItem(createItemRequestDto, user, Optional.of(itemRequest));

        assertThat(item.getId(), equalTo(null));
        assertThat(item.getName(), equalTo("name"));
        assertThat(item.getDescription(), equalTo("desc"));
        assertThat(item.getAvailable(), equalTo(true));
        assertThat(item.getOwner(), equalTo(user));
        assertThat(item.getComments().size(), equalTo(0));
        assertThat(item.getRequest().getId(), equalTo(0L));
    }

    @Test
    void toUserResponseDto() {
        User user = new User(0L, "user", "a@a.com");

        UserResponseDto userResponseDto = mapper.toUserResponseDto(user);
        assertThat(userResponseDto.getId(), equalTo(0L));
        assertThat(userResponseDto.getName(), equalTo("user"));
        assertThat(userResponseDto.getEmail(), equalTo("a@a.com"));
    }

    @Test
    void toUser() {
        CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto("user", "a@a.com");

        User user = mapper.toUser(createUserRequestDto);
        assertThat(user.getId(), equalTo(null));
        assertThat(user.getUsername(), equalTo("user"));
        assertThat(user.getEmail(), equalTo("a@a.com"));
    }

    @Test
    void toItemComment() {
        CreateItemCommentDto createItemCommentDto = new CreateItemCommentDto("comment");
        Item item = new Item();
        User user = new User();

        Comment comment = mapper.toItemComment(createItemCommentDto, user, item);
        assertThat(comment.getId(), equalTo(0L));
        assertThat(comment.getText(), equalTo("comment"));
        assertThat(item, equalTo(item));
        assertThat(user, equalTo(user));
    }

    @Test
    void toItemCommentResponseDto() {
        User user = new User(0L, "user", "a@a.com");
        Item item = new Item(0L, "name", "desc", true, user, Collections.emptySet(), null);
        LocalDateTime created = LocalDateTime.now();
        Comment comment = new Comment(0L, "comment", item, user, created);

        ItemCommentResponseDto itemCommentResponseDto = mapper.toItemCommentResponseDto(comment);
        assertThat(itemCommentResponseDto.getId(), equalTo(0L));
        assertThat(itemCommentResponseDto.getText(), equalTo("comment"));
        assertThat(itemCommentResponseDto.getAuthorName(), equalTo("user"));
        assertThat(itemCommentResponseDto.getCreated(), equalTo(created));
    }

    @Test
    void toItemRequestResponseDto() {
        User user = new User(0L, "user", "a@a.com");
        LocalDateTime created = LocalDateTime.now();
        Set<Item> items = Set.of(new Item());
        ItemRequest itemRequest = new ItemRequest(0L, "desc", user, created, items);

        ItemRequestResponseDto itemRequestResponseDto = mapper.toItemRequestResponseDto(itemRequest);
        assertThat(itemRequestResponseDto.getId(), equalTo(0L));
        assertThat(itemRequestResponseDto.getDescription(), equalTo("desc"));
        assertThat(itemRequestResponseDto.getCreated(), equalTo(created));
    }

    @Test
    void toItemRequestWithItemsResponseDto() {
        User user = new User(0L, "user", "a@a.com");
        LocalDateTime created = LocalDateTime.now();
        Item item = new Item();
        Set<Item> items = Set.of(item);
        ItemRequest itemRequest = new ItemRequest(0L, "desc", user, created, items);
        ItemResponseDto itemResponseDto = new ItemResponseDto();

        doReturn(itemResponseDto).when(mapper).toItemResponseDto(item);
        ItemRequestWithItemsResponseDto itemRequestWithItemsResponseDto = mapper.toItemRequestWithItemsResponseDto(itemRequest);
        assertThat(itemRequestWithItemsResponseDto.getId(), equalTo(0L));
        assertThat(itemRequestWithItemsResponseDto.getDescription(), equalTo("desc"));
        assertThat(itemRequestWithItemsResponseDto.getCreated(), equalTo(created));
        assertThat(itemRequestWithItemsResponseDto.getItems().size(), equalTo(1));
        assertThat(itemRequestWithItemsResponseDto.getItems().contains(itemResponseDto), equalTo(true));
    }

    @Test
    void toItemRequest() {
        CreateItemRequestRequestDto createItemRequestRequestDto = new CreateItemRequestRequestDto("desc");
        User user = new User();

        ItemRequest itemRequest = mapper.toItemRequest(createItemRequestRequestDto, user);
        assertThat(itemRequest.getId(), equalTo(null));
        assertThat(itemRequest.getDescription(), equalTo("desc"));
        assertThat(itemRequest.getRequestor(), equalTo(user));
        assertThat(itemRequest.getItems().size(), equalTo(0));
    }
}