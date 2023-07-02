package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query(value = "select * from bookings where user_id = ? order by start_time desc;", nativeQuery = true)
    List<Booking> findByBookerId(Integer id);

    Page<Booking> findByBooker(User booker, Pageable pageable);

    @Query(value = "select * from bookings where user_id = ? and status = ? order by start_time desc;", nativeQuery = true)
    List<Booking> findByBookerIdAndStatus(Integer id, String status);

    Page<Booking> findByBookerAndStatus(User user, BookingStatus status, Pageable pageable);

    @Query(value = "SELECT * FROM bookings WHERE user_id = ? and start_time < CURRENT_TIMESTAMP AND end_time >" +
            " CURRENT_TIMESTAMP ORDER BY start_time DESC;", nativeQuery = true)
    List<Booking> findByBookerIdAndCurrentTime(Integer id);

    Page<Booking> findByBookerAndStartTimeBeforeAndEndTimeAfter(User booker, LocalDateTime current1,
                                                                LocalDateTime current2, Pageable pageable);

    @Query(value = "SELECT * FROM bookings WHERE user_id = ? and start_time > CURRENT_TIMESTAMP " +
            "ORDER BY start_time DESC;", nativeQuery = true)
    List<Booking> findByBookerIdAndFutureTime(Integer id);

    Page<Booking> findByBookerAndStartTimeAfter(User booker, LocalDateTime current, Pageable pageable);

    @Query(value = "SELECT * FROM bookings WHERE user_id = ? and end_time < CURRENT_TIMESTAMP " +
            "ORDER BY start_time DESC;", nativeQuery = true)
    List<Booking> findByBookerIdAndPastTime(Integer id);

    Page<Booking> findByBookerAndEndTimeBefore(User booker, LocalDateTime current, Pageable pageable);

    @Query(value = "select * from bookings where item_id = ? order by start_time desc;", nativeQuery = true)
    List<Booking> findByItemId(Integer id);

    Page<Booking> findByItem(Item item, Pageable pageable);

    @Query(value = "select * from bookings where item_id = ? and status = ? order by start_time desc;", nativeQuery = true)
    List<Booking> findByItemIdAndStatus(Integer id, String status);

    Page<Booking> findByItemAndStatus(Item item, BookingStatus status, Pageable pageable);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ? and start_time < CURRENT_TIMESTAMP AND end_time > " +
            "CURRENT_TIMESTAMP ORDER BY start_time DESC;", nativeQuery = true)
    List<Booking> findByItemIdAndCurrentTime(Integer id);

    Page<Booking> findByItemAndStartTimeBeforeAndEndTimeAfter(Item item, LocalDateTime current1,
                                                              LocalDateTime current2, Pageable pageable);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ? and start_time > CURRENT_TIMESTAMP " +
            "ORDER BY start_time DESC;", nativeQuery = true)
    List<Booking> findByItemIdAndFutureTime(Integer id);

    Page<Booking> findByItemAndStartTimeAfter(Item item, LocalDateTime current, Pageable pageable);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ? and end_time < CURRENT_TIMESTAMP " +
            "ORDER BY start_time DESC;", nativeQuery = true)
    List<Booking> findByItemIdAndPastTime(Integer id);

    Page<Booking> findByItemAndEndTimeBefore(Item item, LocalDateTime current, Pageable pageable);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ? and start_time > CURRENT_TIMESTAMP AND status <> " +
            "'REJECTED' ORDER BY start_time LIMIT 1;", nativeQuery = true)
    Booking findNextBooking(Integer id);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ? and start_time < CURRENT_TIMESTAMP AND status <> " +
            "'REJECTED' ORDER BY start_time DESC LIMIT 1;", nativeQuery = true)
    Booking findLastBooking(Integer id);

    @Query(value = "select * from bookings where user_id = ? and item_id = ? and end_time < CURRENT_TIMESTAMP AND " +
            "status = 'APPROVED';", nativeQuery = true)
    List<Booking> findByBookerIdAndItemIdInPast(Integer userId, Integer itemId);
}
