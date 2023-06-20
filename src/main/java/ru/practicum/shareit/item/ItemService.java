package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.CommentWithoutBookingException;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemOut;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository,
                       BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    public Item addNewItem(Item item, Integer ownerId) {
        Optional<User> user = userRepository.findById(ownerId);
        if (user.isEmpty()) {
            throw new NotFoundException();
        }
        item.setOwner(user.get());
        return itemRepository.save(item);
    }

    public Item updateItem(Item item, Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException();
        }
        Item oldItem = itemRepository.getReferenceById(item.getId());
        if (!oldItem.getOwner().equals(user.get())) {
            throw new ForbiddenAccessException("Редактировать может только владелец.");
        }
        oldItem.update(item);
        return itemRepository.save(oldItem);
    }

    public ItemOut getItem(Integer id, Integer userId) {
        Optional<Item> item = itemRepository.findById(id);
        Optional<User> user = userRepository.findById(userId);
        if (item.isEmpty() || user.isEmpty()) {
            throw new NotFoundException();
        }
        ItemOut itemOut = toItemOut(item.get());
        if (!item.get().getOwner().getId().equals(userId)) {
            itemOut.setLastBooking(null);
            itemOut.setNextBooking(null);
        }
        return itemOut;
    }

    public List<ItemOut> getAllUsersItems(Integer userId) {
        return collectionToItemOut(itemRepository.findByOwnerId(userId));
    }

    public List<Item> search(String word) {
        if (word == null || word.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findByAvailableTrueAndNameContainingOrAvailableTrueAndDescriptionContainingIgnoreCase(
                word, word);
    }

    public CommentDto addNewComment(CommentDto commentDto, Integer itemId, Integer userId) {
        Optional<Item> item = itemRepository.findById(itemId);
        Optional<User> user = userRepository.findById(userId);
        if (item.isEmpty() || user.isEmpty()) {
            throw new NotFoundException();
        }
        List<Booking> bookings = bookingRepository.findByBookerIdAndUserIdInPast(userId, itemId);
        if (bookings.isEmpty()) {
            throw new CommentWithoutBookingException();
        }
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setAuthor(user.get());
        comment.setItem(item.get());
        comment.setCreatedTime(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
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
        if (nextBooking ==  null) {
            itemOut.setNextBooking(null);
        } else {
            itemOut.setNextBooking(BookingMapper.toBookingDto(nextBooking));
        }
        Booking lastBooking = bookingRepository.findLastBooking(item.getId());
        if (lastBooking ==  null) {
            itemOut.setLastBooking(null);
        } else {
            itemOut.setLastBooking(BookingMapper.toBookingDto(lastBooking));
        }
        itemOut.setComments(CommentMapper.collectionToCommentDto(commentRepository.findByItemId(item.getId())));
        return itemOut;
    }
}
