package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("/test_add_users_items_bookings.sql")
class BookingRepositoryTest {
    @Autowired
    private BookingRepository repository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "startTime");
    private final LocalDateTime now = LocalDateTime.now();
    private final User masha = new User(1, "masha", "email@mail.ru");
    private final User vova = new User(2, "vova", "email1@mail.ru");
    private final User valy = new User(3, "valy", "demo@mail.ru");
    private final Item kukla = new Item(1, "kukla", "igrushka", true, masha, null);
    private final Item nosok = new Item(2, "nosok", "vesch", true, masha, null);
    private Booking kuklaByVova = new Booking(1, now.plusHours(1), now.plusHours(2), kukla, vova,
            BookingStatus.REJECTED);
    private Booking nosokByVova = new Booking(2, now.plusHours(2), now.plusHours(4), nosok, vova,
            BookingStatus.WAITING);
    private Booking kuklaByValy = new Booking(3, now.minusHours(1), now.plusHours(2), kukla, valy,
            BookingStatus.WAITING);
    private Booking nosokByValy = new Booking(4, now.minusHours(4), now.minusHours(2), nosok, valy,
            BookingStatus.APPROVED);

    @BeforeEach
    void getBookings() {
        // Обновляем из-за разницы в секунадах.
        kuklaByVova = repository.getReferenceById(1);
        nosokByVova = repository.getReferenceById(2);
        kuklaByValy = repository.getReferenceById(3);
        nosokByValy = repository.getReferenceById(4);
    }

    @Test
    void findByBookerId() {
        Booking booking = repository.findByBookerId(vova.getId()).get(1);
        assertEquals(kuklaByVova, booking);
    }

    @Test
    void findByBooker() {
        Booking booking = repository.findByBooker(vova, PageRequest.of(1, 1, sort)).toList().get(0);
        assertEquals(kuklaByVova, booking);
        List<Booking> bookings = repository.findByBooker(vova, PageRequest.of(0, 2, sort)).toList();
        assertEquals(2, bookings.size());
        assertEquals(nosokByVova, bookings.get(0));
        assertEquals(kuklaByVova, bookings.get(1));
    }

    @Test
    void findByBookerIdAndStatus() {
        Booking booking = repository.findByBookerIdAndStatus(vova.getId(),"REJECTED").get(0);
        assertEquals(kuklaByVova, booking);
    }

    @Test
    void findByBookerAndStatus() {
        List<Booking> bookings = repository.findByBookerAndStatus(vova, BookingStatus.WAITING,
                PageRequest.of(0, 2, sort)).toList();
        assertEquals(1, bookings.size());
        assertEquals(nosokByVova, bookings.get(0));
    }

    @Test
    void findByBookerIdAndCurrentTime() {
        List<Booking> bookings = repository.findByBookerIdAndCurrentTime(valy.getId());
        assertEquals(1, bookings.size());
        assertEquals(kuklaByValy, bookings.get(0));
    }

    @Test
    void findByBookerAndStartTimeBeforeAndEndTimeAfter() {
        List<Booking> bookings = repository.findByBookerAndStartTimeBeforeAndEndTimeAfter(valy, now, now,
                PageRequest.of(1, 4)).toList();
        assertTrue(bookings.isEmpty());
        bookings = repository.findByBookerAndStartTimeBeforeAndEndTimeAfter(valy, now, now,
                PageRequest.of(0, 4)).toList();
        assertEquals(1, bookings.size());
        assertEquals(kuklaByValy, bookings.get(0));
    }

    @Test
    void findByBookerIdAndFutureTime() {
        List<Booking> bookings = repository.findByBookerIdAndFutureTime(vova.getId());
        assertEquals(2, bookings.size());
        assertEquals(nosokByVova, bookings.get(0));
        assertEquals(kuklaByVova, bookings.get(1));
    }

    @Test
    void findByBookerAndStartTimeAfter() {
        List<Booking> bookings = repository.findByBookerAndStartTimeAfter(vova, now,
                PageRequest.of(1, 1, sort)).toList();
        assertEquals(1, bookings.size());
        assertEquals(kuklaByVova, bookings.get(0));
    }

    @Test
    void findByBookerIdAndPastTime() {
        List<Booking> bookings = repository.findByBookerIdAndPastTime(valy.getId());
        assertEquals(1, bookings.size());
        assertEquals(nosokByValy, bookings.get(0));
    }

    @Test
    void findByBookerAndEndTimeBefore() {
        List<Booking> bookings = repository.findByBookerAndEndTimeBefore(vova, now,
                PageRequest.of(0, 10, sort)).toList();
        assertTrue(bookings.isEmpty());
    }

    @Test
    void findByItemId() {
        List<Booking> bookings = repository.findByItemId(nosok.getId());
        assertEquals(2, bookings.size());
        assertEquals(nosokByVova, bookings.get(0));
        assertEquals(nosokByValy, bookings.get(1));
    }

    @Test
    void findByItem() {
        List<Booking> bookings = repository.findByItem(nosok, PageRequest.of(1, 1, sort)).toList();
        assertEquals(1, bookings.size());
        assertEquals(nosokByValy, bookings.get(0));
    }

    @Test
    void findByItemIdAndStatus() {
        List<Booking> bookings = repository.findByItemIdAndStatus(nosok.getId(), "WAITING");
        assertEquals(1, bookings.size());
        assertEquals(nosokByVova, bookings.get(0));
    }

    @Test
    void findByItemAndStatus() {
        List<Booking> bookings = repository.findByItemAndStatus(nosok, BookingStatus.WAITING,
                PageRequest.of(0, 4, sort)).toList();
        assertEquals(1, bookings.size());
        assertEquals(nosokByVova, bookings.get(0));
    }

    @Test
    void findByItemIdAndCurrentTime() {
        List<Booking> bookings = repository.findByItemIdAndCurrentTime(kukla.getId());
        assertEquals(1, bookings.size());
        assertEquals(kuklaByValy, bookings.get(0));
    }

    @Test
    void findByItemAndStartTimeBeforeAndEndTimeAfter() {
        List<Booking> bookings = repository.findByItemAndStartTimeBeforeAndEndTimeAfter(kukla, now, now,
                PageRequest.of(0, 2, sort)).toList();
        assertEquals(1, bookings.size());
        assertEquals(kuklaByValy, bookings.get(0));
    }

    @Test
    void findByItemIdAndFutureTime() {
        List<Booking> bookings = repository.findByItemIdAndFutureTime(kukla.getId());
        assertEquals(1, bookings.size());
        assertEquals(kuklaByVova, bookings.get(0));
    }

    @Test
    void findByItemAndStartTimeAfter() {
        List<Booking> bookings = repository.findByItemAndStartTimeAfter(kukla, now,
                PageRequest.of(0, 2, sort)).toList();
        assertEquals(1, bookings.size());
        assertEquals(kuklaByVova, bookings.get(0));
    }

    @Test
    void findByItemIdAndPastTime() {
        List<Booking> bookings = repository.findByItemIdAndPastTime(nosok.getId());
        assertEquals(1, bookings.size());
        assertEquals(nosokByValy, bookings.get(0));
    }

    @Test
    void findByItemAndEndTimeBefore() {
        List<Booking> bookings = repository.findByItemAndEndTimeBefore(nosok, now,
                PageRequest.of(0, 2, sort)).toList();
        assertEquals(1, bookings.size());
        assertEquals(nosokByValy, bookings.get(0));
    }

    @Test
    void findNextBooking() {
        Booking booking = repository.findNextBooking(nosok.getId());
        assertEquals(nosokByVova, booking);
    }

    @Test
    void findLastBooking() {
        Booking booking = repository.findLastBooking(nosok.getId());
        assertEquals(nosokByValy, booking);
    }

    @Test
    void findByBookerIdAndItemIdInPast() {
        List<Booking> bookings = repository.findByBookerIdAndItemIdInPast(valy.getId(), nosok.getId());
        assertEquals(1, bookings.size());
        assertEquals(nosokByValy, bookings.get(0));
    }
}