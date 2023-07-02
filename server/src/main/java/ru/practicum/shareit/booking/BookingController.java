package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOut;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public BookingOut addNewBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        bookingDto.setBookerId(userId);
        return service.addNewBooking(bookingDto);
    }

    @PatchMapping("/{id}")
    public BookingOut changeStatus(@RequestHeader(value = "X_SHARER_USER_ID") Long userId, @PathVariable("id") Long id,
                                   @RequestParam(name = "approved") boolean status) {

        return service.changeStatus(Integer.valueOf(String.valueOf(id)), Integer.valueOf(String.valueOf(userId)), status);
    }

    @GetMapping("/{id}")
    public BookingOut getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id) {
        return service.getBooking(id, userId);
    }

    @GetMapping
    public List<BookingOut> getUserBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(name = "state", defaultValue = "ALL") State state,
                                            @RequestParam(name = "from", required = false) Integer from,
                                            @RequestParam(name = "size", required = false) Integer size) {
        if (from == null || size == null) {
            return service.getUserBookings(userId, state);
        }
        return service.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOut> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(name = "state", defaultValue = "ALL") State state,
                                            @RequestParam(name = "from", required = false) Integer from,
                                            @RequestParam(name = "size", required = false) Integer size) {
        if (from == null || size == null) {
            return service.getOwnerBookings(userId, state);
        }
        return service.getOwnerBookings(userId, state, from, size);
    }
}
