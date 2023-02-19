package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Boolean existsBookingByBookerAndItemAndFinishIsBeforeAndStatus(User booker, Item item, LocalDateTime beforeFinish, BookingStatus status);

    // by booker
    List<Booking> findAllByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByBooker_IdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findAllByBooker_Id(Long bookerId, Pageable pageable);

    List<Booking> findByBooker_IdAndFinishIsBefore(Long bookerId, LocalDateTime beforeFinish, Sort sort);

    List<Booking> findByBooker_IdAndFinishIsBefore(Long bookerId, LocalDateTime beforeFinish, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsBeforeAndFinishIsAfter(Long bookerId, LocalDateTime afterStart, LocalDateTime beforeFinish, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndFinishIsAfter(Long bookerId, LocalDateTime afterStart, LocalDateTime beforeFinish, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime afterStart, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime afterStart, Pageable pageable);

    // by items
    List<Booking> findAllByItemInAndStatus(Collection<Item> items, BookingStatus status, Sort sort);

    List<Booking> findAllByItemInAndStatus(Collection<Item> items, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemIn(Collection<Item> items, Sort sort);

    List<Booking> findAllByItemIn(Collection<Item> items, Pageable pageable);

    List<Booking> findByItemInAndFinishIsBefore(Collection<Item> items, LocalDateTime beforeFinish, Sort sort);

    List<Booking> findByItemInAndFinishIsBefore(Collection<Item> items, LocalDateTime beforeFinish, Pageable pageable);

    List<Booking> findByItemInAndStartIsBeforeAndFinishIsAfter(Collection<Item> items, LocalDateTime afterStart, LocalDateTime beforeFinish, Sort sort);

    List<Booking> findByItemInAndStartIsBeforeAndFinishIsAfter(Collection<Item> items, LocalDateTime afterStart, LocalDateTime beforeFinish, Pageable pageable);

    List<Booking> findByItemInAndStartIsAfter(Collection<Item> items, LocalDateTime afterStart, Sort sort);

    List<Booking> findByItemInAndStartIsAfter(Collection<Item> items, LocalDateTime afterStart, Pageable pageable);

    // by single item

    Optional<Booking> findByItemAndFinishIsAfter(Item item, LocalDateTime afterFinish);

    Optional<Booking> findByItemAndStartIsBefore(Item item, LocalDateTime beforeStart);
}
