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
        return service.addNewBooking(bookingDto, userId);
    }

    @PatchMapping("/{id}")
    public BookingOut changeStatus(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id,
                                   @RequestParam(name = "approved") Boolean status) {
        return service.changeStatus(id, userId, status);
    }

    @GetMapping("/{id}")
    public BookingOut getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id) {
        return service.getBooking(id, userId);
    }

    @GetMapping
    public List<BookingOut> getUserBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return service.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingOut> geOwnerBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return service.getOwnerBookings(userId, state);
    }

}
