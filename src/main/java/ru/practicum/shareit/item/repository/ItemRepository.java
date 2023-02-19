package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner_Id(Long ownerId);

    @Query("select i from Item i " +
            "where i.available = true and ( upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')) )")
    List<Item> findAllAvailableTrueAndNameOrDescriptionLikeIgnoreCase(String query, Pageable pageable);

    @Query("select i as item, lb as lastBooking, nb as nextBooking from Item i " +
            "left outer join Booking lb on lb.item = i and lb.finish = (select max(lb1.finish) from Booking lb1 where lb1.item = i and lb1.finish < ?2 order by lb1.id, lb1.finish asc) " +
            "left outer join Booking nb on nb.item = i and nb.start = (select min(nb1.start) from Booking nb1 where nb1.item = i and nb1.start > ?2 order by nb1.id, nb1.start desc) " +
            "where i.available = true and i.owner.id = ?1")
    List<ItemWithBookingProjection> findAllAvailableTrueByOwner_IdWithClosestBookings(Long ownerId, LocalDateTime now, Pageable pageable);
}
