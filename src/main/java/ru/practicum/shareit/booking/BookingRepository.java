package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query(value = "select * from bookings where user_id = ? order by start_time desc;", nativeQuery = true)
    List<Booking> findByBookerId(Integer id);

    @Query(value = "select * from bookings where user_id = ? and status = ? order by start_time desc;", nativeQuery = true)
    List<Booking> findByBookerIdAndStatus(Integer id, String status);

    @Query(value = "SELECT * FROM bookings WHERE user_id = ? and start_time < CURRENT_TIMESTAMP AND end_time >" +
            " CURRENT_TIMESTAMP ORDER BY start_time DESC;", nativeQuery = true)
    List<Booking> findByBookerIdAndCurrentTime(Integer id);

    @Query(value = "SELECT * FROM bookings WHERE user_id = ? and start_time > CURRENT_TIMESTAMP " +
            "ORDER BY start_time DESC;", nativeQuery = true)
    List<Booking> findByBookerIdAndFutureTime(Integer id);

    @Query(value = "SELECT * FROM bookings WHERE user_id = ? and end_time < CURRENT_TIMESTAMP " +
            "ORDER BY start_time DESC;", nativeQuery = true)
    List<Booking> findByBookerIdAndPastTime(Integer id);

    @Query(value = "select * from bookings where item_id = ? order by start_time desc;", nativeQuery = true)
    List<Booking> findByItemId(Integer id);

    @Query(value = "select * from bookings where item_id = ? and status = ? order by start_time desc;", nativeQuery = true)
    List<Booking> findByItemIdAndStatus(Integer id, String status);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ? and start_time < CURRENT_TIMESTAMP AND end_time >" +
            " CURRENT_TIMESTAMP ORDER BY start_time DESC;", nativeQuery = true)
    List<Booking> findByItemIdAndCurrentTime(Integer id);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ? and start_time > CURRENT_TIMESTAMP " +
            "ORDER BY start_time DESC;", nativeQuery = true)
    List<Booking> findByItemIdAndFutureTime(Integer id);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ? and end_time < CURRENT_TIMESTAMP " +
            "ORDER BY start_time DESC;", nativeQuery = true)
    List<Booking> findByItemIdAndPastTime(Integer id);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ? and start_time > CURRENT_TIMESTAMP AND status <> " +
            "'REJECTED' ORDER BY start_time LIMIT 1;", nativeQuery = true)
    Booking findNextBooking(Integer id);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ? and start_time < CURRENT_TIMESTAMP AND status <> " +
            "'REJECTED' ORDER BY start_time DESC LIMIT 1;", nativeQuery = true)
    Booking findLastBooking(Integer id);

    @Query(value = "select * from bookings where user_id = ? and item_id = ? and end_time < CURRENT_TIMESTAMP AND " +
            "status = 'APPROVED';", nativeQuery = true)
    List<Booking> findByBookerIdAndUserIdInPast(Integer userId, Integer itemId);
}
