package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient client;

    public ItemRequestController(ItemRequestClient client) {
        this.client = client;
    }

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @RequestBody @Valid ItemRequestDto requestDto) {
        requestDto.setRequesterId(userId);
        return client.create(requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return client.getByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @RequestParam(name = "from", required = false) @Min(0) Integer from,
                                               @RequestParam(name = "size", required = false) @Min(1) Integer size) {
        if (from == null || size == null) {
            return client.getAll(userId);
        }
        return client.getAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer id) {
        return client.getById(id, userId);
    }
}
