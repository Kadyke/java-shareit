package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOut;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public BookingService(BookingRepository bookingRepository, UserService userService, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    public BookingOut addNewBooking(BookingDto bookingDto, Integer userId) {
        User user = userService.getUser(userId);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(NotFoundException::new);
        if (!item.getAvailable()) {
            throw new UnavailableItemException();
        }
        if (item.getOwner().equals(user)) {
            throw new BookingForSelfException();
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new TimeException("Окончание не может быть раньше начала.");
        }
        if (bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new TimeException("Окончание не может быть одновременно с началом.");
        }
        bookingDto.setBookerId(userId);
        Booking booking = BookingMapper.toBooking(bookingDto,item, user);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingOut(bookingRepository.save(booking));
    }

    public BookingOut changeStatus(Integer id, Integer userId, Boolean status) {
        User user = userService.getUser(userId);
        Booking booking = bookingRepository.findById(id).orElseThrow(NotFoundException::new);
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
        return BookingMapper.toBookingOut(bookingRepository.save(booking));
    }

    public BookingOut getBooking(Integer id, Integer userId) {
        User user = userService.getUser(userId);
        Booking booking = bookingRepository.findById(id).orElseThrow(NotFoundException::new);
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new ForbiddenAccessException("Просмотр доступен только участникам бронирования.");
        }
        return BookingMapper.toBookingOut(booking);
    }

    public List<BookingOut> getUserBookings(Integer userId, State state) {
        User user = userService.getUser(userId);
        switch (state) {
            case ALL:
                return BookingMapper.collectionToBookingOut(bookingRepository.findByBookerId(userId));
            case PAST:
                return BookingMapper.collectionToBookingOut(bookingRepository.findByBookerIdAndPastTime(userId));
            case FUTURE:
                return BookingMapper.collectionToBookingOut(bookingRepository.findByBookerIdAndFutureTime(userId));
            case CURRENT:
                return BookingMapper.collectionToBookingOut(bookingRepository.findByBookerIdAndCurrentTime(userId));
            case WAITING:
            case REJECTED:
                return BookingMapper.collectionToBookingOut(bookingRepository.findByBookerIdAndStatus(userId,
                        state.toString()));
        }
        return null;
    }

    public List<BookingOut> getUserBookings(Integer userId, State state, Integer from, Integer size) {
        User user = userService.getUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "startTime");
        Integer page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return BookingMapper.collectionToBookingOut(bookingRepository.findByBooker(user,
                        pageable).toList());
            case PAST:
                return BookingMapper.collectionToBookingOut(
                        bookingRepository.findByBookerAndEndTimeBefore(user, now, pageable).toList());
            case FUTURE:
                return BookingMapper.collectionToBookingOut(
                        bookingRepository.findByBookerAndStartTimeAfter(user, now, pageable).toList());
            case CURRENT:
                return BookingMapper.collectionToBookingOut(
                        bookingRepository.findByBookerAndStartTimeBeforeAndEndTimeAfter(user, now, now, pageable)
                                .toList());
            case WAITING:
            case REJECTED:
                return BookingMapper.collectionToBookingOut(
                        bookingRepository.findByBookerAndStatus(user, BookingStatus.valueOf(state.toString()),
                                pageable).toList());
        }
        return null;
    }

    public List<BookingOut> getOwnerBookings(Integer userId, State state) {
        User user = userService.getUser(userId);;
        List<Item> items = itemRepository.findByOwnerId(userId);
        if (items.isEmpty()) {
            throw new NotFoundException();
        }
        List<BookingOut> bookings = new ArrayList<>();
        for (Item item : items) {
            switch (state) {
                case ALL:
                    bookings.addAll(BookingMapper.collectionToBookingOut(bookingRepository.findByItemId(item.getId())));
                    break;
                case PAST:
                    bookings.addAll(BookingMapper.collectionToBookingOut(
                            bookingRepository.findByItemIdAndPastTime(item.getId())));
                    break;
                case FUTURE:
                    bookings.addAll(BookingMapper.collectionToBookingOut(
                            bookingRepository.findByItemIdAndFutureTime(item.getId())));
                    break;
                case CURRENT:
                    bookings.addAll(BookingMapper.collectionToBookingOut(
                            bookingRepository.findByItemIdAndCurrentTime(item.getId())));
                    break;
                case WAITING:
                case REJECTED:
                    bookings.addAll(BookingMapper.collectionToBookingOut(
                            bookingRepository.findByItemIdAndStatus(item.getId(),
                            state.toString())));
                    break;
            }
        }
        return bookings.stream().sorted((o1, o2) -> {
            if (o1.getStart().isBefore(o2.getStart())) {
                return 1;
            } else if (o1.getStart().equals(o2.getStart())) {
                return 0;
            }
            return -1;
        }).collect(toList());
    }

    public List<BookingOut> getOwnerBookings(Integer userId, State state, Integer from, Integer size) {
        User user = userService.getUser(userId);
        List<Item> items = itemRepository.findByOwnerId(userId);
        if (items.isEmpty()) {
            throw new NotFoundException();
        }
        List<BookingOut> bookings = new ArrayList<>();
        for (Item item : items) {
            switch (state) {
                case ALL:
                    bookings.addAll(BookingMapper.collectionToBookingOut(bookingRepository.findByItemId(item.getId())));
                    break;
                case PAST:
                    bookings.addAll(BookingMapper.collectionToBookingOut(
                            bookingRepository.findByItemIdAndPastTime(item.getId())));
                    break;
                case FUTURE:
                    bookings.addAll(BookingMapper.collectionToBookingOut(
                            bookingRepository.findByItemIdAndFutureTime(item.getId())));
                    break;
                case CURRENT:
                    bookings.addAll(BookingMapper.collectionToBookingOut(
                            bookingRepository.findByItemIdAndCurrentTime(item.getId())));
                    break;
                case WAITING:
                case REJECTED:
                    bookings.addAll(BookingMapper.collectionToBookingOut(
                            bookingRepository.findByItemIdAndStatus(item.getId(),
                                    state.toString())));
                    break;
            }
        }
        return bookings.stream().sorted((o1, o2) -> {
            if (o1.getStart().isBefore(o2.getStart())) {
                return 1;
            } else if (o1.getStart().equals(o2.getStart())) {
                return 0;
            }
            return -1;
        }).collect(toList()).subList(from, from + size);
    }
}
