package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOut;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    BookingService service;
    @Autowired
    MockMvc mvc;
    private final LocalDateTime now = LocalDateTime.now();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private User masha;
    private User vova;
    private User valy;
    private Item kukla;
    private BookingDto bookingDto;
    private BookingOut bookingOut;
    @BeforeEach
    void getArguments() {
        masha = new User(1, "masha", "email@mail.ru");
        vova = new User(2, "vova", "email1@mail.ru");
        valy = new User(3, "valy", "demo@mail.ru");
        kukla = new Item(1, "kukla", "igrushka", true, masha, null);
        bookingDto = new BookingDto();
        bookingDto.setItemId(kukla.getId());
        bookingDto.setStart(now.plusHours(1));
        bookingDto.setEnd(now.plusHours(2));
        bookingOut = new BookingOut(1, now.plusHours(1), now.plusHours(2), ItemMapper.toItemDto(kukla),
                UserMapper.toUserDto(vova), BookingStatus.WAITING);
    }
    @Test
    @SneakyThrows
    void addNewBooking() {
        when(service.addNewBooking(bookingDto, vova.getId())).thenAnswer(invocationOnMock -> {
            Booking booking = BookingMapper.toBooking(invocationOnMock.getArgument(0), kukla, vova);
            booking.setId(1);
            booking.setStatus(BookingStatus.WAITING);
            return BookingMapper.toBookingOut(booking);
        });
        mvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", vova.getId())
                .content(mapper.writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOut.getId())))
                .andExpect(jsonPath("$.start", is(bookingOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(bookingOut.getStatus().name())));
    }

    @Test
    @SneakyThrows
    void changeStatus() {
        bookingDto.setId(1);
        bookingOut.setStatus(BookingStatus.APPROVED);
        when(service.changeStatus(bookingDto.getId(), masha.getId(), true)).thenAnswer(invocationOnMock -> {
            Booking booking = BookingMapper.toBooking(bookingDto, kukla, vova);
            booking.setStatus(BookingStatus.APPROVED);
            return BookingMapper.toBookingOut(booking);
        });
        mvc.perform(patch("/bookings/{id}", 1)
                        .header("X-Sharer-User-Id", masha.getId())
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOut.getId())))
                .andExpect(jsonPath("$.start", is(bookingOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(bookingOut.getStatus().name())));
    }

    @Test
    @SneakyThrows
    void getBooking() {
        when(service.getBooking(bookingOut.getId(), masha.getId())).thenAnswer(invocationOnMock -> bookingOut);
        mvc.perform(get("/bookings/{id}", 1)
                        .header("X-Sharer-User-Id", masha.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOut.getId())))
                .andExpect(jsonPath("$.start", is(bookingOut.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(bookingOut.getStatus().name())));
    }

    @Test
    @SneakyThrows
    void getUserBookings() {
        when(service.getUserBookings(vova.getId(), State.ALL, 0, 2))
                .thenAnswer(invocationOnMock -> List.of(bookingOut));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", vova.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingOut.getId())))
                .andExpect(jsonPath("$[0].start", is(bookingOut.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(bookingOut.getStatus().name())));
    }

    @Test
    @SneakyThrows
    void geOwnerBookings() {
        when(service.getOwnerBookings(masha.getId(), State.ALL, 0, 2))
                .thenAnswer(invocationOnMock -> List.of(bookingOut));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", masha.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingOut.getId())))
                .andExpect(jsonPath("$[0].start", is(bookingOut.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingOut.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(bookingOut.getStatus().name())));
    }
}