package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOut;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Autowired
    @InjectMocks
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    private final User masha = new User(1, "masha", "email@mail.ru");
    private final User vova = new User(2, "vova", "email1@mail.ru");
    private final User valy = new User(3, "valy", "demo@mail.ru");
    private final ItemRequest requestByValy = new ItemRequest(1, "hochu igrushku dly devochki", valy,
            null);
    private final ItemRequest requestByVova = new ItemRequest(2, "hochu pistolet", vova, null);
    private final Item kukla = new Item(1, "kukla", "vesch", false, masha, requestByValy);
    private final Item nosok = new Item(2, "nosok", "vesch", true, masha, null);
    private final Item pistol = new Item(3, "pistol", "oruzhie", true, valy, requestByVova);
    private final Item shlypa = new Item(4, "shlypa", "pistolet", true, vova, requestByValy);


    @Test
    void addNewItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("chto-to");
        itemDto.setDescription("secret");
        itemDto.setAvailable(true);
        itemDto.setRequestId(requestByValy.getId());
        when(userService.getUser(vova.getId())).thenReturn(vova);
        when(requestRepository.findById(requestByValy.getId())).thenReturn(Optional.of(requestByValy));
        when(itemRepository.save(Mockito.any(Item.class))).thenAnswer(invocationOnMock -> {
            Item item = invocationOnMock.getArgument(0);
            item.setId(1);
            return item;
        });
        ItemDto item = itemService.addNewItem(itemDto, vova.getId());
        assertNotNull(item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(itemDto.getRequestId(), item.getRequestId());
    }

    @Test
    void updateItem() {
        ItemDto itemDto = ItemMapper.toItemDto(kukla);
        itemDto.setAvailable(true);
        itemDto.setName("kukolka");
        itemDto.setDescription(null);
        when(userService.getUser(masha.getId())).thenReturn(masha);
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(kukla));
        when(requestRepository.findById(requestByValy.getId())).thenReturn(Optional.of(requestByValy));
        when(itemRepository.save(Mockito.any(Item.class))).thenAnswer(invocationOnMock ->
                invocationOnMock.getArgument(0));
        Item item = itemService.updateItem(itemDto, masha.getId());
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertNotEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(itemDto.getRequestId(), item.getRequest().getId());
    }

    @Test
    void getItem() {
        when(userService.getUser(masha.getId())).thenReturn(masha);
        when(itemRepository.findById(kukla.getId())).thenReturn(Optional.of(kukla));
        when(bookingRepository.findNextBooking(Mockito.anyInt())).thenReturn(null);
        when(bookingRepository.findLastBooking(Mockito.anyInt())).thenReturn(null);
        when(commentRepository.findByItemId(Mockito.anyInt())).thenReturn(new ArrayList<>());
        ItemOut itemOut = itemService.getItem(kukla.getId(), masha.getId());
        assertEquals(kukla.getId(), itemOut.getId());
        assertEquals(kukla.getName(), itemOut.getName());
        assertEquals(kukla.getDescription(), itemOut.getDescription());
        assertEquals(kukla.getAvailable(), itemOut.getAvailable());
        assertNotNull(itemOut.getComments());
    }

    @Test
    void getAllUsersItems() {
        when(userService.getUser(masha.getId())).thenReturn(masha);
        when(itemRepository.findByOwnerId(masha.getId())).thenReturn(List.of(kukla, nosok));
        List<ItemOut> items = itemService.getAllUsersItems(masha.getId());
        assertEquals(2, items.size());
    }

    @Test
    void search() {
        when(itemRepository.findByAvailableTrueAndNameContainingOrAvailableTrueAndDescriptionContainingIgnoreCase(
                "pist", "pist")).thenReturn(List.of(pistol, shlypa));
        List<Item> items = itemService.search("pist");
        assertEquals(2, items.size());
    }

    @Test
    void addNewComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Chet ne och");
        when(userService.getUser(vova.getId())).thenReturn(vova);
        when(itemRepository.findById(nosok.getId())).thenReturn(Optional.of(nosok));
        when(bookingRepository.findByBookerIdAndItemIdInPast(vova.getId(), nosok.getId())).thenReturn(List.of(
                new Booking()));
        when(commentRepository.save(Mockito.any(Comment.class))).thenAnswer(invocationOnMock ->
                invocationOnMock.getArgument(0));
        CommentDto comment = itemService.addNewComment(commentDto,nosok.getId(), vova.getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(vova.getName(), comment.getAuthorName());
        assertEquals(ItemMapper.toItemDto(nosok), comment.getItem());
        assertNotNull(comment.getCreated());
    }
}