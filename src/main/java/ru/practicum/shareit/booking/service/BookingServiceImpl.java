package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.SearchBookingStatus;
import ru.practicum.shareit.booking.exception.BookingItemThatUserOwns;
import ru.practicum.shareit.booking.exception.ImmutableBookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.generic.ExtendedEntityNotFoundException;
import ru.practicum.shareit.item.exception.ItemIsUnavailable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.ModelMapper;
import ru.practicum.shareit.security.facade.IAuthenticationFacade;
import ru.practicum.shareit.security.user.ExtendedUserDetails;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final IAuthenticationFacade authenticationFacade;

    private final ModelMapper mapper;

    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, IAuthenticationFacade authenticationFacade, ModelMapper mapper) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.authenticationFacade = authenticationFacade;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ResponseBookingDto create(CreateBookingDto createBookingDto) {
        Item item = itemRepository
                .findById(createBookingDto.getItemId())
                .orElseThrow(() -> new ExtendedEntityNotFoundException(Item.class, createBookingDto.getItemId()));

        if (!item.getAvailable()) {
            throw new ItemIsUnavailable(item);
        }

        User currentUser = authenticationFacade.getCurrentUser();

        if (item.getOwner().getId().equals(currentUser.getId())) {
            throw new BookingItemThatUserOwns(currentUser, item);
        }

        Booking booking = bookingRepository.save(mapper.toBooking(createBookingDto, item, currentUser));

        return mapper.toResponseBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseBookingDto findOne(Long bookingId) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ExtendedEntityNotFoundException(Booking.class, bookingId));

        checkBookingIsAccessible(booking);

        return mapper.toResponseBookingDto(booking);
    }

    @Override
    @Transactional
    public ResponseBookingDto updateStatus(Long bookingId, BookingStatus newStatus) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ExtendedEntityNotFoundException(Booking.class, bookingId));

        checkBookingItemOwnership(booking);

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ImmutableBookingStatus(booking);
        }

        booking.setStatus(newStatus);

        return mapper.toResponseBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseBookingDto> findAllBookedByCurrentUserByStatusOrderByDateDesc(SearchBookingStatus status) {
        ExtendedUserDetails currentUserDetails = authenticationFacade.getCurrentUserDetails();

        List<Booking> bookings = new ArrayList<>();

        if (status.equals(SearchBookingStatus.ALL)) {
            bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(currentUserDetails.getId());
        } else if (status.equals(SearchBookingStatus.CURRENT)) {
            bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndFinishIsAfter(
                    currentUserDetails.getId(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    Sort.by(new Sort.Order(Sort.Direction.DESC, "start"))
            );
        } else if (status.equals(SearchBookingStatus.PAST)) {
            bookings = bookingRepository.findByBooker_IdAndFinishIsBefore(
                    currentUserDetails.getId(),
                    LocalDateTime.now(),
                    Sort.by(new Sort.Order(Sort.Direction.DESC, "start"))
            );
        } else if (status.equals(SearchBookingStatus.FUTURE)) {
            bookings = bookingRepository.findByBooker_IdAndStartIsAfter(
                    currentUserDetails.getId(),
                    LocalDateTime.now(),
                    Sort.by(new Sort.Order(Sort.Direction.DESC, "start"))
            );
        } else if (status.equals(SearchBookingStatus.WAITING)) {
            bookings = bookingRepository.findAllByBooker_IdAndStatusOrderByStart(
                    currentUserDetails.getId(),
                    BookingStatus.WAITING
            );
        } else if (status.equals(SearchBookingStatus.REJECTED)) {
            bookings = bookingRepository.findAllByBooker_IdAndStatusOrderByStart(
                    currentUserDetails.getId(),
                    BookingStatus.REJECTED
            );
        }

        return bookings
                .stream()
                .map(mapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseBookingDto> findAllForCurrentUserItemsByStatusOrderByDateDesc(SearchBookingStatus status) {
        User currentUser = authenticationFacade.getCurrentUser();

        List<Booking> bookings = new ArrayList<>();

        if (status.equals(SearchBookingStatus.ALL)) {
            bookings = bookingRepository.findAllByItemInOrderByStartDesc(currentUser.getItems());
        } else if (status.equals(SearchBookingStatus.CURRENT)) {
            bookings = bookingRepository.findByItemInAndStartIsBeforeAndFinishIsAfter(
                    currentUser.getItems(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    Sort.by(new Sort.Order(Sort.Direction.DESC, "start"))
            );
        } else if (status.equals(SearchBookingStatus.PAST)) {
            bookings = bookingRepository.findByItemInAndFinishIsBefore(
                    currentUser.getItems(),
                    LocalDateTime.now(),
                    Sort.by(new Sort.Order(Sort.Direction.DESC, "start"))
            );
        } else if (status.equals(SearchBookingStatus.FUTURE)) {
            bookings = bookingRepository.findByItemInAndStartIsAfter(
                    currentUser.getItems(),
                    LocalDateTime.now(),
                    Sort.by(new Sort.Order(Sort.Direction.DESC, "start"))
            );
        } else if (status.equals(SearchBookingStatus.WAITING)) {
            bookings = bookingRepository.findAllByItemInAndStatusOrderByStart(currentUser.getItems(), BookingStatus.WAITING);
        } else if (status.equals(SearchBookingStatus.REJECTED)) {
            bookings = bookingRepository.findAllByItemInAndStatusOrderByStart(currentUser.getItems(), BookingStatus.REJECTED);
        }

        return bookings
                .stream()
                .map(mapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }

    private void checkBookingItemOwnership(Booking booking) {
        ExtendedUserDetails currentUserDetails = authenticationFacade.getCurrentUserDetails();

        if (!booking.getItem().getOwner().getId().equals(currentUserDetails.getId())) {
            throw new AccessDeniedException(
                    String.format(
                            "User with id '%d' tried to interact with booking with id '%d'. Rejected.",
                            currentUserDetails.getId(),
                            booking.getId()
                    )
            );
        }
    }

    private void checkBookingIsAccessible(Booking booking) {
        ExtendedUserDetails currentUserDetails = authenticationFacade.getCurrentUserDetails();

        Boolean bookingOwnerCondition = booking.getBooker().getId().equals(currentUserDetails.getId());
        Boolean itemOwnerCondition = booking.getItem().getOwner().getId().equals(currentUserDetails.getId());

        if (!bookingOwnerCondition && !itemOwnerCondition) {
            throw new AccessDeniedException(
                    String.format(
                            "User with id '%d' tried to interact with booking with id '%d'. Rejected.",
                            currentUserDetails.getId(),
                            booking.getId()
                    )
            );
        }
    }
}
