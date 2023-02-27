package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository repository;

    @Test
    void findAllAvailableTrueAndNameOrDescriptionLikeIgnoreCase() {
        User user = entityManager.persist(new User(null, "", ""));

        Item item1 = entityManager.persist(new Item(null, "query", "", true, user, Collections.emptySet(), null));
        Item item2 = entityManager.persist(new Item(null, "", "query", true, user, Collections.emptySet(), null));
        Item item3 = entityManager.persist(new Item(null, "qUeRy", "", true, user, Collections.emptySet(), null));
        Item item4 = entityManager.persist(new Item(null, "", "qUeRy", true, user, Collections.emptySet(), null));
        Item item5 = entityManager.persist(new Item(null, "test query test", "", true, user, Collections.emptySet(), null));
        Item item6 = entityManager.persist(new Item(null, "", "test query test", true, user, Collections.emptySet(), null));
        Item item7 = entityManager.persist(new Item(null, "test", "test", true, user, Collections.emptySet(), null));
        Item item8 = entityManager.persist(new Item(null, "", "", true, user, Collections.emptySet(), null));

        List<Item> items = repository.findAllAvailableTrueAndNameOrDescriptionLikeIgnoreCase("query", Pageable.unpaged());

        assertThat(items).hasSize(6).contains(item1, item2, item3, item4, item5, item6);
    }

    @Test
    void findAllAvailableTrueByOwner_IdWithClosestBookings() {
        User user = entityManager.persist(new User(null, "", ""));

        Item item = entityManager.persist(new Item(null, "query", "", true, user, Collections.emptySet(), null));

        Booking booking1 = entityManager.persist(new Booking(null, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4), item, user, BookingStatus.APPROVED));
        Booking booking2 = entityManager.persist(new Booking(null, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), item, user, BookingStatus.APPROVED));
        Booking booking3 = entityManager.persist(new Booking(null, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), item, user, BookingStatus.APPROVED));
        Booking booking4 = entityManager.persist(new Booking(null, LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(5), item, user, BookingStatus.APPROVED));

        List<ItemWithBookingProjection> items = repository.findAllAvailableTrueByOwner_IdWithClosestBookings(user.getId(), LocalDateTime.now(), Pageable.unpaged());

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getItem()).isEqualTo(item);
        assertThat(items.get(0).getLastBooking()).isEqualTo(booking2);
        assertThat(items.get(0).getNextBooking()).isEqualTo(booking3);
    }
}