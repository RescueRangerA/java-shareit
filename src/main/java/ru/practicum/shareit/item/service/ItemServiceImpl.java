package ru.practicum.shareit.item.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.item.exception.NotAllowedToAddComments;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemCommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.security.user.ExtendedUserDetails;
import ru.practicum.shareit.security.facade.IAuthenticationFacade;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;

    private final ItemCommentRepository itemCommentRepository;

    private final ModelMapper mapper;

    private final IAuthenticationFacade authenticationFacade;

    public ItemServiceImpl(ItemRepository itemRepository, BookingRepository bookingRepository, ItemCommentRepository itemCommentRepository, ModelMapper mapper, IAuthenticationFacade authenticationFacade) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.itemCommentRepository = itemCommentRepository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseWithBookingDto> findAll() {
        ExtendedUserDetails currentUserDetails = authenticationFacade.getCurrentUserDetails();
        List<Item> items = itemRepository.findAllAvailableTrueByOwner_Id(currentUserDetails.getId());

        return items
                .stream()
                .map((item -> mapper.toItemResponseWithBookingDto(
                        item,
                        bookingRepository.findByItemAndStartIsBefore(item, LocalDateTime.now()),
                        bookingRepository.findByItemAndFinishIsAfter(item, LocalDateTime.now())
                )
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> findByText(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.findAllAvailableTrueAndNameOrDescriptionLikeIgnoreCase(text);

        return items
                .stream()
                .map(mapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseWithBookingDto findOne(Long itemId) {
        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(() -> new ExtendedEntityNotFoundException(Item.class, itemId));

        Optional<Booking> lastBooking = Optional.empty();
        Optional<Booking> nextBooking = Optional.empty();

        if (canReadBookingsOfItem(item)) {
            lastBooking = bookingRepository.findByItemAndStartIsBefore(item, LocalDateTime.now());
            nextBooking = bookingRepository.findByItemAndFinishIsAfter(item, LocalDateTime.now());
        }

        return mapper.toItemResponseWithBookingDto(item, lastBooking, nextBooking);
    }

    @Override
    @Transactional
    public ItemResponseDto create(CreateItemRequestDto createItemRequestDto) {
        User currentUser = authenticationFacade.getCurrentUser();

        return mapper.toItemResponseDto(itemRepository.save(mapper.toItem(createItemRequestDto, currentUser)));
    }

    @Override
    @Transactional
    public ItemResponseDto update(Long itemId, UpdateItemRequestDto updateItemRequestDto) {
        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(() -> new ExtendedEntityNotFoundException(Item.class, itemId));

        checkItemOwnershipOrThrow(item);

        if (updateItemRequestDto.getName() != null) {
            item.setName(updateItemRequestDto.getName());
        }

        if (updateItemRequestDto.getDescription() != null) {
            item.setDescription(updateItemRequestDto.getDescription());
        }

        if (updateItemRequestDto.getAvailable() != null) {
            item.setAvailable(updateItemRequestDto.getAvailable());
        }

        return mapper.toItemResponseDto(item);
    }

    @Override
    @Transactional
    public ItemCommentResponseDto addComment(Long itemId, CreateItemCommentDto createItemCommentDto) {
        User currentUser = authenticationFacade.getCurrentUser();

        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(() -> new ExtendedEntityNotFoundException(Item.class, itemId));


        Boolean canAddComment = bookingRepository.existsBookingByBookerAndItemAndFinishIsBeforeAndStatus(
                currentUser,
                item,
                LocalDateTime.now(),
                BookingStatus.APPROVED
        );

        if (!canAddComment) {
            throw new NotAllowedToAddComments(currentUser, item);
        }

        Comment comment = itemCommentRepository.save(mapper.toItemComment(createItemCommentDto, currentUser, item));

        return mapper.toItemCommentResponseDto(comment);
    }

    @Override
    @Transactional
    public void removeById(Long itemId) {
        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(() -> new ExtendedEntityNotFoundException(Item.class, itemId));

        checkItemOwnershipOrThrow(item);
        itemRepository.deleteById(itemId);
    }

    private void checkItemOwnershipOrThrow(Item item) {
        ExtendedUserDetails currentUserDetails = authenticationFacade.getCurrentUserDetails();

        if (!item.getOwner().getId().equals(currentUserDetails.getId())) {
            throw new AccessDeniedException(
                    String.format(
                            "User with id '%d' tried to update item with id '%d'. Rejected.",
                            currentUserDetails.getId(),
                            item.getOwner().getId()
                    )
            );
        }
    }

    private Boolean canReadBookingsOfItem(Item item) {
        ExtendedUserDetails currentUserDetails = authenticationFacade.getCurrentUserDetails();

        return item.getOwner().getId().equals(currentUserDetails.getId());
    }
}
