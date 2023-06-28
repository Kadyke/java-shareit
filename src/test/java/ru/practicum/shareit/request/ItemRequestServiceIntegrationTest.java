package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql("/test_add_users_items_requests.sql")
@Transactional
class ItemRequestServiceIntegrationTest {
    @Autowired
    private ItemRequestService service;
    @Autowired
    private ItemRequestRepository repository;
    private final LocalDateTime now = LocalDateTime.now();
    private final User masha = new User(1, "masha", "email@mail.ru");
    private final User vova = new User(2, "vova", "email1@mail.ru");
    private final User valy = new User(3, "valy", "demo@mail.ru");
    private ItemRequest requestByValy = new ItemRequest(1, "hochu igrushku dly devochki", valy,
            now.minusHours(1));
    private ItemRequest requestByVova = new ItemRequest(2, "hochu pistolet", vova, now);
    private final Item kukla = new Item(1, "kukla", "vesch", false, masha, requestByValy);
    private final Item nosok = new Item(2, "nosok", "vesch", true, masha, null);
    private final Item pistol = new Item(3, "pistol", "oruzhie", true, valy, requestByVova);
    private final Item shlypa = new Item(4, "shlypa", "pistolet", true, vova, requestByValy);

    @BeforeEach
    void getTrueCreatedTime() {
        requestByValy = repository.getReferenceById(requestByValy.getId());
        requestByVova = repository.getReferenceById(requestByVova.getId());
    }
    @Test
    void getUserRequests() {
        List<ItemRequestDto> requests = service.getUserRequests(valy.getId());
        assertEquals(1, requests.size());
        assertEquals(requestByValy.getId(), requests.get(0).getId());
        assertEquals(2, requests.get(0).getItems().size());
    }

    @Test
    void getAllRequests() {
        List<ItemRequestDto> requests = service.getAllRequests(masha.getId());
        assertEquals(2, requests.size());
        assertEquals(requestByVova.getId(), requests.get(0).getId());
        assertEquals(requestByValy.getId(), requests.get(1).getId());
        assertEquals(1, requests.get(0).getItems().size());
        assertEquals(2, requests.get(1).getItems().size());
    }

    @Test
    void getAllRequestsByPages() {
        List<ItemRequestDto> requests = service.getAllRequests(masha.getId(), 1 ,1);
        assertEquals(1, requests.size());
        assertEquals(requestByValy.getId(), requests.get(0).getId());
        assertEquals(2, requests.get(0).getItems().size());
    }
}