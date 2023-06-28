package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    @Autowired
    @InjectMocks
    private ItemRequestService service;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock
    private ItemRequestRepository repository;
    private final LocalDateTime now = LocalDateTime.now();
    private final Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
    private final User vova = new User(2, "vova", "email1@mail.ru");
    private final User valy = new User(3, "valy", "demo@mail.ru");
    private ItemRequest requestByValy = new ItemRequest(1, "hochu igrushku dly devochki", valy, now);
    private ItemRequest requestByVova = new ItemRequest(2, "hochu pistolet", vova, now.plusHours(1));
    
    @Test
    void addNewRequest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("ne znayu chego hochu");
        itemRequestDto.setRequesterId(vova.getId());
        when(userService.getUser(vova.getId())).thenReturn(vova);
        when(repository.save(Mockito.any(ItemRequest.class))).thenAnswer(invocationOnMock -> {
            ItemRequest request = invocationOnMock.getArgument(0);
            request.setId(3);
            return request;
        });
        ItemRequestDto request = service.addNewRequest(itemRequestDto);
        assertNotNull(request.getId());
        assertEquals(itemRequestDto.getDescription(), request.getDescription());
        assertEquals(itemRequestDto.getRequesterId(), request.getRequesterId());
        assertNotNull(request.getCreated());
        assertNull(request.getItems());
    }

    @Test
    void getUserRequests() {
        when(userService.getUser(vova.getId())).thenReturn(vova);
        when(repository.findByUserId(vova.getId())).thenReturn(List.of(requestByVova));
        when(itemService.findByRequestId(Mockito.anyInt())).thenReturn(new ArrayList<>());
        List<ItemRequestDto> requests = service.getUserRequests(vova.getId());
        assertEquals(1, requests.size());
        assertEquals(requestByVova.getId(), requests.get(0).getId());
        assertTrue(requests.get(0).getItems().isEmpty());
    }

    @Test
    void getAllRequests() {
        when(userService.getUser(vova.getId())).thenReturn(vova);
        when(repository.findByRequesterNot(vova, sort)).thenReturn(List.of(requestByValy));
        when(itemService.findByRequestId(Mockito.anyInt())).thenReturn(new ArrayList<>());
        List<ItemRequestDto> requests = service.getAllRequests(vova.getId());
        assertEquals(1, requests.size());
        assertEquals(requestByValy.getId(), requests.get(0).getId());
        assertTrue(requests.get(0).getItems().isEmpty());
    }

    @Test
    void getRequest() {
        when(userService.getUser(vova.getId())).thenReturn(vova);
        when(repository.findById(requestByVova.getId())).thenReturn(Optional.of(requestByVova));
        when(itemService.findByRequestId(Mockito.anyInt())).thenReturn(new ArrayList<>());
        ItemRequestDto request = service.getRequest(vova.getId(), requestByVova.getId());
        assertEquals(requestByVova.getId(), request.getId());
        assertTrue(request.getItems().isEmpty());
    }
}