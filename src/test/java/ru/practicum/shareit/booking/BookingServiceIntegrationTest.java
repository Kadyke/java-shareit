package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOut;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql("/test_add_users_items_bookings.sql")
@Transactional
class BookingServiceIntegrationTest {
    @Autowired
    private BookingService bookingService;
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

    @Test
    @Sql("/test_add_users_items.sql")
    @Transactional
    void addNewBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(nosok.getId());
        bookingDto.setStart(now);
        bookingDto.setEnd(now.plusHours(1));
        bookingDto.setBookerId(valy.getId());
        BookingOut bookingOut = bookingService.addNewBooking(bookingDto);
        assertEquals(bookingDto.getStart(), bookingOut.getStart());
        assertEquals(bookingDto.getEnd(), bookingOut.getEnd());
        assertEquals(bookingDto.getItemId(), bookingOut.getItem().getId());
        assertEquals(UserMapper.toUserDto(valy), bookingOut.getBooker());
        assertEquals(BookingStatus.WAITING, bookingOut.getStatus());
    }

    @Test
    @Transactional
    void changeStatus() {
        BookingOut bookingAfter = bookingService.changeStatus(nosokByVova.getId(), masha.getId(), true);
        assertEquals(BookingStatus.APPROVED, bookingAfter.getStatus());
    }

    @Test
    @Transactional
    void getBooking() {
        BookingOut bookingOut = bookingService.getBooking(nosokByVova.getId(), vova.getId());
        assertEquals(nosokByVova.getId(), bookingOut.getId());
        assertEquals(ItemMapper.toItemDto(nosok), bookingOut.getItem());
        assertEquals(UserMapper.toUserDto(vova), bookingOut.getBooker());
        assertEquals(BookingStatus.WAITING, bookingOut.getStatus());
    }

    @Test
    @Transactional
    void getUserBookings() {
        List<BookingOut> bookings = bookingService.getUserBookings(vova.getId(), State.ALL);
        assertEquals(2, bookings.size());
        assertEquals(nosokByVova.getId(), bookings.get(0).getId());
        assertEquals(kuklaByVova.getId(), bookings.get(1).getId());
    }

    @Test
    @Transactional
    void getUserBookingsByPage() {
        List<BookingOut> bookings = bookingService.getUserBookings(vova.getId(), State.ALL, 1, 1);
        assertEquals(1, bookings.size());
        assertEquals(kuklaByVova.getId(), bookings.get(0).getId());
    }

    @Test
    @Transactional
    void getOwnerBookings() {
        List<BookingOut> bookings = bookingService.getOwnerBookings(masha.getId(), State.ALL);
        System.out.println(bookings);
        assertEquals(4, bookings.size());
        assertEquals(nosokByVova.getId(), bookings.get(0).getId());
        assertEquals(kuklaByVova.getId(), bookings.get(1).getId());
        assertEquals(kuklaByValy.getId(), bookings.get(2).getId());
        assertEquals(nosokByValy.getId(), bookings.get(3).getId());
    }

    @Test
    void getOwnerBookingsByPage() {
        List<BookingOut> bookings = bookingService.getOwnerBookings(masha.getId(), State.ALL, 0, 3);
        System.out.println(bookings);
        assertEquals(3, bookings.size());
        assertEquals(nosokByVova.getId(), bookings.get(0).getId());
        assertEquals(kuklaByVova.getId(), bookings.get(1).getId());
        assertEquals(kuklaByValy.getId(), bookings.get(2).getId());
    }
}