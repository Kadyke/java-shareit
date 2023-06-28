package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOut;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Autowired
    @InjectMocks
    private BookingService service;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserService userService;
    @Mock
    ItemRepository itemRepository;
    private final LocalDateTime now = LocalDateTime.now();
    private User masha;
    private User vova;
    private Item kukla;
    private BookingDto bookingDto;
    private Booking booking;
    private User valy;

    @BeforeEach
    void getArguments() {
        masha = new User(1, "masha", "email@mail.ru");
        vova = new User(2, "vova", "email1@mail.ru");
        valy = new User(3, "valy", "demo@mail.ru");
        kukla = new Item(1, "kukla", "igrushka", true, masha, null);
        bookingDto = new BookingDto();
        bookingDto.setItemId(1);
        bookingDto.setStart(now);
        bookingDto.setEnd(now.plusHours(1));
        booking = new Booking(1, now.plusHours(1), now.plusHours(2), kukla, vova, BookingStatus.WAITING);
    }

    @Test
    void addNewBooking_whenBookingIsValid_thenReturnBookingOut() {
        when(userService.getUser(2)).thenReturn(vova);
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(kukla));
        when(bookingRepository.save(Mockito.any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        BookingOut bookingOut = service.addNewBooking(bookingDto, 2);
        assertEquals(bookingDto.getStart(), bookingOut.getStart());
        assertEquals(bookingDto.getEnd(), bookingOut.getEnd());
        assertEquals(ItemMapper.toItemDto(kukla), bookingOut.getItem());
        assertEquals(UserMapper.toUserDto(vova), bookingOut.getBooker());
        assertEquals(BookingStatus.WAITING, bookingOut.getStatus());
    }

    @Test
    void addNewBooking_whenBookingIsNotValidTime_thenReturnTimeException() {
        bookingDto.setStart(now);
        bookingDto.setEnd(now);
        when(userService.getUser(2)).thenReturn(vova);
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(kukla));
        assertThrows(TimeException.class, () -> service.addNewBooking(bookingDto, 2));
        bookingDto.setStart(now);
        bookingDto.setEnd(now.minusHours(1));
        assertThrows(TimeException.class, () -> service.addNewBooking(bookingDto, 2));
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }

    @Test
    void addNewBooking_whenBookingIsNotAvailable_thenReturnUnavailableItemException() {
        kukla.setAvailable(false);
        when(userService.getUser(2)).thenReturn(vova);
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(kukla));;
        assertThrows(UnavailableItemException.class, () -> service.addNewBooking(bookingDto, 2));
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }

    @Test
    void addNewBooking_whenBookingIsForSelf_thenReturnBookingForSelfException() {
        when(userService.getUser(1)).thenReturn(masha);
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(kukla));;
        assertThrows(BookingForSelfException.class, () -> service.addNewBooking(bookingDto, 1));
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }

    @Test
    void changeStatus_whenUserIsOwner_thenReturnBookingApproved() {
        when(userService.getUser(1)).thenReturn(masha);
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(Mockito.any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        BookingOut bookingOut = service.changeStatus(1, 1, true);
        assertEquals(BookingStatus.APPROVED, bookingOut.getStatus());
    }

    @Test
    void changeStatus_whenUserIsNotOwner_thenReturnForbiddenAccessException() {
        when(userService.getUser(2)).thenReturn(vova);
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        assertThrows(ForbiddenAccessException.class, () -> service.changeStatus(1, 2, true));
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }

    @Test
    void changeStatus_whenStatusIsNotWaiting_thenReturnAllReadyChangedStatusException() {
        booking.setStatus(BookingStatus.REJECTED);
        when(userService.getUser(1)).thenReturn(masha);
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        assertThrows(AllReadyChangedStatusException.class, () -> service.changeStatus(1, 1, true));
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }

    @Test
    void getBooking_whenUserIsOwner_thenReturnBookingOut() {
        when(userService.getUser(1)).thenReturn(masha);
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        BookingOut bookingOut = service.getBooking(1, 1);
        assertEquals(booking.getStartTime(), bookingOut.getStart());
        assertEquals(booking.getEndTime(), bookingOut.getEnd());
        assertEquals(ItemMapper.toItemDto(booking.getItem()), bookingOut.getItem());
        assertEquals(UserMapper.toUserDto(booking.getBooker()), bookingOut.getBooker());
        assertEquals(BookingStatus.WAITING, bookingOut.getStatus());
    }

    @Test
    void getBooking_whenUserIsBooker_thenReturnBookingOut() {
        when(userService.getUser(2)).thenReturn(vova);
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        BookingOut bookingOut = service.getBooking(1, 2);
        assertEquals(booking.getStartTime(), bookingOut.getStart());
        assertEquals(booking.getEndTime(), bookingOut.getEnd());
        assertEquals(ItemMapper.toItemDto(booking.getItem()), bookingOut.getItem());
        assertEquals(UserMapper.toUserDto(booking.getBooker()), bookingOut.getBooker());
        assertEquals(BookingStatus.WAITING, bookingOut.getStatus());
    }

    @Test
    void getBooking_whenUserIsNotBookerOrOwner_thenReturnForbiddenAccessException() {
        when(userService.getUser(3)).thenReturn(valy);
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        assertThrows(ForbiddenAccessException.class, () -> service.getBooking(1, 3));
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }

    @Test
    void getUserBookings_whenStateIsAll_thenReturnListOfBookingOut() {
        when(userService.getUser(2)).thenReturn(vova);
        when(bookingRepository.findByBookerId(2)).thenReturn(List.of(booking));
        List<BookingOut> bookings = service.getUserBookings(2, State.ALL);
        verify(bookingRepository, times(1)).findByBookerId(2);
        verify(bookingRepository, never()).findByBookerIdAndPastTime(2);
        verify(bookingRepository, never()).findByBookerIdAndFutureTime(2);
        verify(bookingRepository, never()).findByBookerIdAndCurrentTime(2);
        verify(bookingRepository, never()).findByBookerIdAndStatus(2, State.ALL.toString());
    }

    @Test
    void getOwnerBookings_whenStateIsPast_thenReturnListOfBookingOut() {
        when(userService.getUser(1)).thenReturn(masha);
        when(itemRepository.findByOwnerId(1)).thenReturn(List.of(kukla));
        when(bookingRepository.findByItemIdAndPastTime(1)).thenReturn(List.of(booking));
        List<BookingOut> bookings = service.getOwnerBookings(1, State.PAST);
        verify(bookingRepository, never()).findByItemId(1);
        verify(bookingRepository, times(1)).findByItemIdAndPastTime(1);
        verify(bookingRepository, never()).findByItemIdAndFutureTime(1);
        verify(bookingRepository, never()).findByItemIdAndCurrentTime(1);
        verify(bookingRepository, never()).findByItemIdAndStatus(1, State.ALL.toString());
    }
}