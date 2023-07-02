package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOut;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
        return service.addNewItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody ItemDto itemDto,
                              @PathVariable Integer id) {
        itemDto.setId(id);
        return ItemMapper.toItemDto(service.updateItem(itemDto, userId));
    }

    @GetMapping("/{id}")
    public ItemOut getItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id) {
        return service.getItem(id, userId);
    }

    @GetMapping()
    public List<ItemOut> getAllUsersItems(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @RequestParam(name = "from", required = false) Integer from,
                                          @RequestParam(name = "size", required = false) Integer size) {
        if (from == null || size == null) {
            return service.getAllUsersItems(userId);
        }
        return service.getAllUsersItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String word,
                                @RequestParam(name = "from", required = false) Integer from,
                                @RequestParam(name = "size", required = false) Integer size) {
        if (from == null || size == null) {
            return ItemMapper.collectionToItemDto(service.search(word));
        }
        return ItemMapper.collectionToItemDto(service.search(word, from, size));
    }

    @PostMapping("/{id}/comment")
    public CommentDto addNewComment(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id,
                                 @Valid @RequestBody CommentDto commentDto) {
        return service.addNewComment(commentDto, id, userId);
    }
}
