package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.validation.StateValid;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingClient client;

    public BookingController(BookingClient client) {
        this.client = client;
    }

    @PostMapping
    public ResponseEntity<Object> addNewBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @Valid @RequestBody BookingDto bookingDto) {
        bookingDto.setBookerId(userId);
        return client.create(bookingDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> changeStatus(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @PathVariable Long id,
                                               @RequestParam(name = "approved") Boolean status) {
        return client.changeStatus(id, userId, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id) {
        return client.getById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(name = "state", defaultValue = "ALL") @StateValid String state,
                                            @RequestParam(name = "from", required = false) @Min(0) Integer from,
                                            @RequestParam(name = "size", required = false) @Min(1) Integer size) {
        if (from == null || size == null) {
            return client.getByUser(userId, state);
        }
        return client.getByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(name = "state", defaultValue = "ALL") @StateValid String state,
                                            @RequestParam(name = "from", required = false) @Min(0) Integer from,
                                            @RequestParam(name = "size", required = false) @Min(1) Integer size) {
        if (from == null || size == null) {
            return client.getByOwner(userId, state);
        }
        return client.getByOwner(userId, state, from, size);
    }
}
