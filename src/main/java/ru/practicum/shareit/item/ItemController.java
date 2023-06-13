package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto item) {
        return itemService.addNewItem(userId, item);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody ItemDto itemDto,
                              @PathVariable Integer id) {
        itemDto.setId(id);
        return itemService.updateItem(userId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable Integer id) {
        return itemService.getItem(id);
    }

    @GetMapping()
    public List<ItemDto> getAllUsersItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAllUsersItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String word) {
        return itemService.search(word);
    }
}
