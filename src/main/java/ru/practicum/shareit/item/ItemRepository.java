package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import java.util.*;

@Component
public class ItemRepository {
    private final static HashMap<Integer, HashMap<Integer, Item>> items = new HashMap<>();
    private Integer id = 0;

    public Item addNewItem(Integer userId, Item item) {
        item.setId(++id);
        item.setOwnerId(userId);
        if (items.containsKey(userId)) {
            items.get(userId).put(item.getId(), item);
        } else {
            HashMap<Integer, Item> newItem = new HashMap<>();
            newItem.put(item.getId(), item);
            items.put(userId, newItem);
        }
        return item;
    }

    public Item updateItem(Item item) {
        if (items.containsKey(item.getOwnerId()) && items.get(item.getOwnerId()).containsKey(item.getId())) {
            Item oldItem = items.get(item.getOwnerId()).get(item.getId());
            oldItem.update(item);
            return oldItem;
        } else {
            throw new NotFoundException();
        }
    }

    public Item getItem(Integer id) {
        for (HashMap<Integer, Item> usersItems : items.values())  {
            if (usersItems.containsKey(id)) {
                return usersItems.get(id);
            }
        }
        throw new NotFoundException();
    }

    public Collection<Item> getAllUsersItems(Integer userId) {
        return items.get(userId).values();
    }

    public List<Item> search(String word) {
        if (word == null || word.isBlank()) {
            return new ArrayList<>();
        }
        word = word.toLowerCase();
        List<Item> itemsBySearch = new ArrayList<>();
        for (HashMap<Integer, Item> usersItems : items.values())  {
            for (Item item : usersItems.values()) {
                String name = item.getName().toLowerCase();
                String description = item.getDescription().toLowerCase();
                if ((name.contains(word) || description.contains(word)) && item.getAvailable()) {
                    itemsBySearch.add(item);
                }
            }
        }
        return itemsBySearch;
    }
}
