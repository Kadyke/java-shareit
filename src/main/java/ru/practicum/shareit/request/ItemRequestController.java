package ru.practicum.shareit.request;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService service;

    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ItemRequestDto addNewRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                        @RequestBody @Valid ItemRequestDto requestDto) {
        requestDto.setRequesterId(userId);
        return service.addNewRequest(requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return service.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @RequestParam(name = "from", required = false) @Min(0) Integer from,
                                               @RequestParam(name = "size", required = false) @Min(1) Integer size) {
        if (from == null || size == null) {
            return service.getAllRequests(userId);
        }
        return service.getAllRequests(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer id) {
        return service.getRequest(userId, id);
    }
}
