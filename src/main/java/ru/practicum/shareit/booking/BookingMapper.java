package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOut;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class BookingMapper {
    public BookingOut toBookingOut(Booking booking) {
        BookingOut bookingOut = new BookingOut();
        bookingOut.setId(booking.getId());
        bookingOut.setEnd(booking.getEndTime());
        bookingOut.setStart(booking.getStartTime());
        bookingOut.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingOut.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingOut.setStatus(booking.getStatus());
        return bookingOut;
    }

    public BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setEnd(booking.getEndTime());
        bookingDto.setStart(booking.getStartTime());
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setBookerId(booking.getBooker().getId());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public List<BookingOut> collectionToBookingOut(Collection<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingOut).collect(toList());
    }

    public Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStartTime(bookingDto.getStart());
        booking.setEndTime(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }
}
