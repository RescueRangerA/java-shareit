package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.ShortResponseBookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ModelMapper {
    public Booking toBooking(CreateBookingDto createBookingDto, Item item, User user) {
        return new Booking(
                0L,
                createBookingDto.getStart(),
                createBookingDto.getEnd(),
                item,
                user,
                BookingStatus.WAITING
        );
    }

    public ResponseBookingDto toResponseBookingDto(Booking booking) {
        return new ResponseBookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getFinish(),
                booking.getStatus(),
                this.toUserResponseDto(booking.getBooker()),
                this.toItemResponseDto(booking.getItem())
        );
    }

    public ShortResponseBookingForItemDto toResponseShortBookingDto(Booking booking) {
        return new ShortResponseBookingForItemDto(
                booking.getId(),
                booking.getStart(),
                booking.getFinish(),
                booking.getBooker().getId()
        );
    }

    public ItemResponseDto toItemResponseDto(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getComments().stream().map(this::toItemCommentResponseDto).collect(Collectors.toSet())
        );
    }

    public ItemResponseWithBookingDto toItemResponseWithBookingDto(Item item, Optional<Booking> lastBooking, Optional<Booking> nextBooking) {
        return new ItemResponseWithBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                lastBooking.map(this::toResponseShortBookingDto).orElse(null),
                nextBooking.map(this::toResponseShortBookingDto).orElse(null),
                item.getComments().stream().map(this::toItemCommentResponseDto).collect(Collectors.toSet())
        );
    }

    public Item toItem(CreateItemRequestDto itemRequestDto, User user) {
        return new Item(
                null,
                itemRequestDto.getName(),
                itemRequestDto.getDescription(),
                itemRequestDto.getAvailable(),
                user,
                Collections.emptySet()
        );
    }

    public UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public User toUser(CreateUserRequestDto user) {
        return new User(
                null,
                user.getName(),
                user.getEmail()
        );
    }

    public Comment toItemComment(CreateItemCommentDto createItemCommentDto, User user, Item item) {
        return new Comment(
                0L,
                createItemCommentDto.getText(),
                item,
                user,
                LocalDateTime.now()
        );
    }

    public ItemCommentResponseDto toItemCommentResponseDto(Comment comment) {
        return new ItemCommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getUsername(),
                comment.getCreated()
        );
    }
}
