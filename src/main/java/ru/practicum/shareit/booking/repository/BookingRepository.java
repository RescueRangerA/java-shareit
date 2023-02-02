package ru.practicum.shareit.booking.repository;

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
    List<Booking> findAllByBooker_IdAndStatusOrderByStart(Long bookerId, BookingStatus status);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findByBooker_IdAndFinishIsBefore(Long bookerId, LocalDateTime beforeFinish, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndFinishIsAfter(Long bookerId, LocalDateTime afterStart, LocalDateTime beforeFinish, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime afterStart, Sort sort);

    // by items
    List<Booking> findAllByItemInAndStatusOrderByStart(Collection<Item> items, BookingStatus status);

    List<Booking> findAllByItemInOrderByStartDesc(Collection<Item> items);

    List<Booking> findByItemInAndFinishIsBefore(Collection<Item> items, LocalDateTime beforeFinish, Sort sort);

    List<Booking> findByItemInAndStartIsBeforeAndFinishIsAfter(Collection<Item> items, LocalDateTime afterStart, LocalDateTime beforeFinish, Sort sort);

    List<Booking> findByItemInAndStartIsAfter(Collection<Item> items, LocalDateTime afterStart, Sort sort);

    // by single item

    Optional<Booking> findByItemAndFinishIsAfter(Item item, LocalDateTime afterFinish);

    Optional<Booking> findByItemAndStartIsBefore(Item item, LocalDateTime beforeStart);
}
