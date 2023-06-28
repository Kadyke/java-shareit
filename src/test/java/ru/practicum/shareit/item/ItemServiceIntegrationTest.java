package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.CommentWithoutBookingException;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOut;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql("/test_add_users_items_bookings_requests.sql")
class ItemServiceIntegrationTest {
    @Autowired
    private ItemService service;
    private final LocalDateTime now = LocalDateTime.now();
    private final User masha = new User(1, "masha", "email@mail.ru");
    private final User vova = new User(2, "vova", "email1@mail.ru");
    private final User valy = new User(3, "valy", "demo@mail.ru");
    private final ItemRequest requestByValy = new ItemRequest(1, "hochu igrushku dly devochki", valy, now);
    private final ItemRequest requestByVova = new ItemRequest(2, "hochu pistolet", vova, now.plusHours(1));
    private final Item kukla = new Item(2, "kukla", "vesch", false, masha, requestByValy);
    private final Item nosok = new Item(3, "nosok", "vesch", true, masha, null);
    private final Item pistol = new Item(4, "pistol", "oruzhie", true, valy, requestByVova);
    private final Item shlypa = new Item(5, "shlypa", "pistolet", true, vova, requestByValy);
    private final Booking kuklaByVova = new Booking(1, now.plusHours(1), now.plusHours(2), kukla, vova,
            BookingStatus.REJECTED);
    private final Booking nosokByVova = new Booking(2, now.plusHours(2), now.plusHours(4), nosok, vova,
            BookingStatus.WAITING);
    private final Booking kuklaByValy = new Booking(3, now.minusHours(1), now.plusHours(2), kukla, valy,
            BookingStatus.WAITING);
    private final Booking nosokByValy = new Booking(4, now.minusHours(4), now.minusHours(2), nosok, valy,
            BookingStatus.APPROVED);

    @Test
    void addNewItem_whenUserIsNotFound_thenThrowNotFoundException() {
        ItemDto itemDto = new ItemDto();
        assertThrows(NotFoundException.class, () -> service.addNewItem(itemDto, 10));
    }

    @Test
    @DirtiesContext
    void addNewItem_whenRequestIsNotNull_thenReturnItem() {
        ItemDto itemDto = new ItemDto(null, "vesch", "dly teby", true, requestByValy.getId());
        ItemDto item = service.addNewItem(itemDto, vova.getId());
        assertNotNull(item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(itemDto.getRequestId(), item.getRequestId());
    }

    @Test
    @Transactional
    void updateItem_whenUserIsNotOwner_thenForbiddenAccessException() {
        ItemDto itemDto = ItemMapper.toItemDto(kukla);
        assertThrows(ForbiddenAccessException.class, () -> service.updateItem(itemDto, vova.getId()));
    }

    @Test
    @Transactional
    void getItemByOtherUsers() {
        ItemOut itemByOwner = service.getItem(nosok.getId(), masha.getId());
        ItemOut itemNotByOwner = service.getItem(nosok.getId(), vova.getId());
        assertEquals(itemByOwner.getId(), itemNotByOwner.getId());
        assertEquals(itemByOwner.getName(), itemNotByOwner.getName());
        assertEquals(itemByOwner.getDescription(), itemNotByOwner.getDescription());
        assertEquals(itemByOwner.getAvailable(), itemNotByOwner.getAvailable());
        assertNotEquals(itemByOwner.getLastBooking(), itemNotByOwner.getLastBooking());
        assertNotEquals(itemByOwner.getNextBooking(), itemNotByOwner.getNextBooking());
    }

    @Test
    void getAllUsersItemsByPages() {
        List<ItemOut> items = service.getAllUsersItems(masha.getId(), 1, 1);
        assertEquals(1, items.size());
    }

    @Test
    @Transactional
    void searchByPages() {
        List<Item> items = service.search("pist", 0, 3);
        assertEquals(2, items.size());
    }

    @Test
    @Transactional
    void addNewComment_whenBookingInFuture_thenCommentWithoutBookingException() {
        CommentDto commentDto = new CommentDto();
        assertThrows(CommentWithoutBookingException.class, () -> service.addNewComment(commentDto, nosok.getId(),
                vova.getId()));
    }
}