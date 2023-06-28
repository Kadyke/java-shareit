package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.List;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequestDto toItemRequestDto(ItemRequest request) {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setRequesterId(request.getRequester().getId());
        requestDto.setCreated(request.getCreatedTime());
        return requestDto;
    }

    public ItemRequest toItemRequest(ItemRequestDto requestDto, User user) {
        ItemRequest request = new ItemRequest();
        request.setId(requestDto.getId());
        request.setDescription(requestDto.getDescription());
        request.setRequester(user);
        request.setCreatedTime(requestDto.getCreated());
        return request;
    }

    public List<ItemRequestDto> collectionToItemRequestDto(List<ItemRequest> requests) {
        return requests.stream().map(ItemRequestMapper::toItemRequestDto).collect(toList());
    }
}
