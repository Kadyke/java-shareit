package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.ArrayList;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient client;

    public ItemController(ItemClient client) {
        this.client = client;
    }

    @PostMapping
    public ResponseEntity<Object> addNewItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        return client.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestBody ItemDto itemDto, @PathVariable Integer id) {
        itemDto.setId(id);
        return client.update(itemDto, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id) {
        return client.getById(id, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllUsersItems(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @RequestParam(name = "from", required = false) @Min(0) Integer from,
                                          @RequestParam(name = "size", required = false) @Min(1) Integer size) {
        if (from == null || size == null) {
            return client.getAllByUser(userId);
        }
        return client.getAllByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(name = "text") String word,
                                @RequestParam(name = "from", required = false) @Min(0) Integer from,
                                @RequestParam(name = "size", required = false) @Min(1) Integer size) {
        if (word == null || word.isBlank()) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }
        if (from == null || size == null) {
            return client.search(word);
        }
        return client.search(word, from, size);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addNewComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @PathVariable Integer id, @Valid @RequestBody CommentDto commentDto) {
        return client.addComment(commentDto, id, userId);
    }
}
