package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOut;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public BookingOut addNewBooking(BookingDto bookingDto, Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (user.isEmpty() || item.isEmpty()) {
            throw new NotFoundException();
        }
        if (!item.get().getAvailable()) {
            throw new UnavailableItemException();
        }
        if (item.get().getOwner().equals(user.get())) {
            throw new BookingForSelfException();
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new TimeException("Окончание не может быть раньше начала.");
        }
        if (bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new TimeException("Окончание не может быть одновременно с началом.");
        }
        bookingDto.setBookerId(userId);
        Booking booking = toBooking(bookingDto);
        booking.setStatus(BookingStatus.WAITING);;
        return toBookingOut(bookingRepository.save(booking));
    }

    public BookingOut changeStatus(Integer id, Integer userId, Boolean status) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (user.isEmpty() || bookingOpt.isEmpty()) {
            throw new NotFoundException();
        }
        Booking booking = bookingOpt.get();
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenAccessException("Менять статус может только владелец.");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new AllReadyChangedStatusException();
        }
        if (status) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return toBookingOut(bookingRepository.save(booking));
    }

    public BookingOut getBooking(Integer id, Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (user.isEmpty() || bookingOpt.isEmpty()) {
            throw new NotFoundException();
        }
        Booking booking = bookingOpt.get();
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new ForbiddenAccessException("Просмотр доступен только участникам бронирования.");
        }
        return toBookingOut(booking);
    }

    public List<BookingOut> getUserBookings(Integer userId, String stateInString) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException();
        }
        State state;
        try {
            state = State.valueOf(stateInString);
        } catch (IllegalArgumentException e) {
            throw new StateException(stateInString);
        }
        switch (state) {
            case ALL:
                return collectionToBookingOut(bookingRepository.findByBookerId(userId));
            case PAST:
                return collectionToBookingOut(bookingRepository.findByBookerIdAndPastTime(userId));
            case FUTURE:
                return collectionToBookingOut(bookingRepository.findByBookerIdAndFutureTime(userId));
            case CURRENT:
                return collectionToBookingOut(bookingRepository.findByBookerIdAndCurrentTime(userId));
            case WAITING:
            case REJECTED:
                return collectionToBookingOut(bookingRepository.findByBookerIdAndStatus(userId, state.toString()));
        }
        return null;
    }

    public List<BookingOut> getOwnerBookings(Integer userId, String stateInString) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException();
        }
        List<Item> items = itemRepository.findByOwnerId(userId);
        if (items.isEmpty()) {
            throw new NotFoundException();
        }
        State state;
        try {
            state = State.valueOf(stateInString);
        } catch (IllegalArgumentException e) {
            throw new StateException(stateInString);
        }
        List<BookingOut> bookings = new ArrayList<>();
        for (Item item : items) {
            switch (state) {
                case ALL:
                    bookings.addAll(collectionToBookingOut(bookingRepository.findByItemId(item.getId())));
                    break;
                case PAST:
                    bookings.addAll(collectionToBookingOut(bookingRepository.findByItemIdAndPastTime(item.getId())));
                    break;
                case FUTURE:
                    bookings.addAll(collectionToBookingOut(bookingRepository.findByItemIdAndFutureTime(item.getId())));
                    break;
                case CURRENT:
                    bookings.addAll(collectionToBookingOut(bookingRepository.findByItemIdAndCurrentTime(item.getId())));
                    break;
                case WAITING:
                case REJECTED:
                    bookings.addAll(collectionToBookingOut(bookingRepository.findByItemIdAndStatus(item.getId(),
                            state.toString())));
                    break;
            }
        }
        return bookings;
    }

    private Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStartTime(bookingDto.getStart());
        booking.setEndTime(bookingDto.getEnd());
        booking.setItem(itemRepository.getReferenceById(bookingDto.getItemId()));
        booking.setBooker(userRepository.getReferenceById(bookingDto.getBookerId()));
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    private List<BookingOut> collectionToBookingOut(Collection<Booking> bookings) {
        return bookings.stream().map(this::toBookingOut).collect(toList());
    }

    private BookingOut toBookingOut(Booking booking) {
        BookingOut bookingOut = new BookingOut();
        bookingOut.setId(booking.getId());
        bookingOut.setEnd(booking.getEndTime());
        bookingOut.setStart(booking.getStartTime());
        bookingOut.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingOut.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingOut.setStatus(booking.getStatus());
        return bookingOut;
    }
}
