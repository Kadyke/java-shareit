package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOut;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(service.addNewItem(item, userId));
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody ItemDto itemDto,
                              @PathVariable Integer id) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(id);
        return ItemMapper.toItemDto(service.updateItem(item, userId));
    }

    @GetMapping("/{id}")
    public ItemOut getItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id) {
        return service.getItem(id, userId);
    }

    @GetMapping()
    public List<ItemOut> getAllUsersItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return service.getAllUsersItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String word) {
        return ItemMapper.collectionToItemDto(service.search(word));
    }

    @PostMapping("/{id}/comment")
    public CommentDto addNewComment(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id,
                                 @Valid @RequestBody CommentDto commentDto) {
        return service.addNewComment(commentDto, id, userId);
    }
}
