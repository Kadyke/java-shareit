package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    public ItemRequestService(ItemRequestRepository repository, UserService userService, ItemService itemService) {
        this.repository = repository;
        this.userService = userService;
        this.itemService = itemService;
    }

    public ItemRequestDto addNewRequest(ItemRequestDto requestDto) {
        User user = userService.getUser(requestDto.getRequesterId());
        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto, user);
        request.setCreatedTime(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(repository.save(request));
    }

    public List<ItemRequestDto> getUserRequests(Integer userId) {
        User user = userService.getUser(userId);
        List<ItemRequestDto> requests = ItemRequestMapper.collectionToItemRequestDto(repository.findByUserId(userId));
        for (ItemRequestDto request : requests) {
            request.setItems(ItemMapper.collectionToItemDto(itemService.findByRequestId(request.getId())));
        }
        return requests;
    }

    public List<ItemRequestDto> getAllRequests(Integer userId) {
        User user = userService.getUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        List<ItemRequestDto> requests = ItemRequestMapper.collectionToItemRequestDto(
                repository.findByRequesterNot(user, sort));
        for (ItemRequestDto request : requests) {
            request.setItems(ItemMapper.collectionToItemDto(itemService.findByRequestId(request.getId())));
        }
        return requests;
    }

    public List<ItemRequestDto> getAllRequests(Integer userId, Integer from, Integer size) {
        User user = userService.getUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        Integer page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        List<ItemRequestDto> requests = ItemRequestMapper.collectionToItemRequestDto(
                repository.findByRequesterNot(user, pageable).toList());
        for (ItemRequestDto request : requests) {
            request.setItems(ItemMapper.collectionToItemDto(itemService.findByRequestId(request.getId())));
        }
        return requests;
    }

    public ItemRequestDto getRequest(Integer userId, Integer id) {
        User user = userService.getUser(userId);
        ItemRequest request = repository.findById(id).orElseThrow(NotFoundException::new);
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);
        requestDto.setItems(ItemMapper.collectionToItemDto(itemService.findByRequestId(requestDto.getId())));
        return requestDto;
    }
}
