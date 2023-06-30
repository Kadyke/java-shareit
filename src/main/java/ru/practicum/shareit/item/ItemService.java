package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.CommentWithoutBookingException;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOut;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    public ItemService(ItemRepository itemRepository, UserService userService,
                       BookingRepository bookingRepository, CommentRepository commentRepository,
                       ItemRequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.requestRepository = requestRepository;
    }

    public ItemDto addNewItem(ItemDto itemDto, Integer ownerId) {
        User user = userService.getUser(ownerId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = requestRepository.findById(itemDto.getRequestId()).orElseThrow(NotFoundException::new);
            item.setRequest(request);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public Item updateItem(ItemDto itemDto, Integer userId) {
        User user = userService.getUser(userId);
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(NotFoundException::new);
        if (!item.getOwner().equals(user)) {
            throw new ForbiddenAccessException("Редактировать может только владелец.");
        }
        if (itemDto.getRequestId() != null) {
            ItemRequest request = requestRepository.findById(itemDto.getRequestId()).orElseThrow(NotFoundException::new);
            item.setRequest(request);
        }
        Item changedItem = ItemMapper.toItem(itemDto);
        item.update(changedItem);
        return itemRepository.save(item);
    }

    public ItemOut getItem(Integer id, Integer userId) {
        Item item = itemRepository.findById(id).orElseThrow(NotFoundException::new);
        User user = userService.getUser(userId);
        ItemOut itemOut = toItemOut(item);
        if (!item.getOwner().getId().equals(userId)) {
            itemOut.setLastBooking(null);
            itemOut.setNextBooking(null);
        }
        return itemOut;
    }

    public List<ItemOut> getAllUsersItems(Integer userId) {
        User user = userService.getUser(userId);
        return collectionToItemOut(itemRepository.findByOwnerId(userId));
    }

    public List<ItemOut> getAllUsersItems(Integer userId, Integer from, Integer size) {
        User user = userService.getUser(userId);
        Integer page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        return collectionToItemOut(itemRepository.findByOwner(user, pageable).toList());
    }

    public List<Item> search(String word) {
        if (word == null || word.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findByAvailableTrueAndNameContainingOrAvailableTrueAndDescriptionContainingIgnoreCase(
                word, word);
    }

    public List<Item> search(String word, Integer from, Integer size) {
        if (word == null || word.isBlank()) {
            return new ArrayList<>();
        }
        Integer page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        return itemRepository.findByAvailableTrueAndNameContainingOrAvailableTrueAndDescriptionContainingIgnoreCase(
                word, word, pageable).toList();
    }

    public CommentDto addNewComment(CommentDto commentDto, Integer itemId, Integer userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(NotFoundException::new);
        User user = userService.getUser(userId);
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdInPast(userId, itemId);
        if (bookings.isEmpty()) {
            throw new CommentWithoutBookingException();
        }
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreatedTime(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    public List<Item> findByRequestId(Integer id) {
        return itemRepository.findByRequestId(id);
    }

    private List<ItemOut> collectionToItemOut(Collection<Item> items) {
        return items.stream().map(this::toItemOut).collect(toList());
    }

    private ItemOut toItemOut(Item item) {
        ItemOut itemOut = new ItemOut();
        itemOut.setId(item.getId());
        itemOut.setName(item.getName());
        itemOut.setDescription(item.getDescription());
        itemOut.setAvailable(item.getAvailable());
        Booking nextBooking = bookingRepository.findNextBooking(item.getId());
        if (nextBooking != null) {
            itemOut.setNextBooking(BookingMapper.toBookingDto(nextBooking));
        }
        Booking lastBooking = bookingRepository.findLastBooking(item.getId());
        if (lastBooking !=  null) {
            itemOut.setLastBooking(BookingMapper.toBookingDto(lastBooking));
        }
        itemOut.setComments(CommentMapper.collectionToCommentDto(commentRepository.findByItemId(item.getId())));
        return itemOut;
    }
}
