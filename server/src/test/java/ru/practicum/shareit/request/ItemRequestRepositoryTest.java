package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("/test_add_users_requests.sql")
class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository repository;
    private final LocalDateTime now = LocalDateTime.now();
    private final Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
    private final User masha = new User(1, "masha", "email@mail.ru");
    private final User vova = new User(2, "vova", "email1@mail.ru");
    private final User valy = new User(3, "valy", "demo@mail.ru");
    private ItemRequest requestByValy = new ItemRequest(1, "hochu igrushku dly devochki", valy, now);
    private ItemRequest requestByVova = new ItemRequest(2, "hochu pistolet", vova, now.plusHours(1));

    @BeforeEach
    void getCurrentTime() {
        requestByValy = repository.getReferenceById(1);
        requestByVova = repository.getReferenceById(2);
    }

    @Test
    void findByUserId() {
        List<ItemRequest> requests = repository.findByUserId(vova.getId());
        assertEquals(1, requests.size());
        assertEquals(requestByVova, requests.get(0));
    }

    @Test
    void findByRequesterNot() {
        List<ItemRequest> requests = repository.findByRequesterNot(masha, sort);
        assertEquals(2, requests.size());
        assertEquals(requestByVova, requests.get(0));
        assertEquals(requestByValy, requests.get(1));
    }

    @Test
    void findByRequesterNotByPage() {
        List<ItemRequest> requests = repository.findByRequesterNot(masha, PageRequest.of(1,1, sort)).toList();
        assertEquals(1, requests.size());
        assertEquals(requestByValy, requests.get(0));
    }
}