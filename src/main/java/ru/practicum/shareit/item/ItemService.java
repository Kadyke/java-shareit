package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public ItemDto addNewItem(Integer userId, ItemDto itemDto) {
        userRepository.getUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        itemDto = ItemMapper.toItemDto(itemRepository.addNewItem(userId, item));
        return itemDto;
    }

    public ItemDto updateItem(Integer userId, ItemDto itemDto) {
        userRepository.getUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        itemDto = ItemMapper.toItemDto(itemRepository.updateItem(item));
        return itemDto;
    }

    public ItemDto getItem(Integer id) {
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.getItem(id));
        return itemDto;
    }


    public List<ItemDto> getAllUsersItems(Integer userId) {
        List<ItemDto> usersItems = new ArrayList<>();
        for (Item item : itemRepository.getAllUsersItems(userId)) {
            usersItems.add(ItemMapper.toItemDto(item));
        }
        return usersItems;
    }

    public List<ItemDto> search(String word) {
        return itemRepository.search(word).stream().map(ItemMapper::toItemDto).collect(toList());
    }
}
