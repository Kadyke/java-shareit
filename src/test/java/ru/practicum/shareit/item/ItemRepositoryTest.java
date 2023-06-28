package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("/test_add_users_items_requests_with_null.sql")
class ItemRepositoryTest {
    @Autowired
    ItemRepository repository;
    private final Sort sort = Sort.by(Sort.Direction.ASC, "id");
    private final User masha = new User(1, "masha", "email@mail.ru");
    private final User vova = new User(2, "vova", "email1@mail.ru");
    private final User valy = new User(3, "valy", "demo@mail.ru");
    private final ItemRequest requestByValy = new ItemRequest(1, "hochu igrushku dly devochki", valy,
            null);
    private final ItemRequest requestByVova = new ItemRequest(2, "hochu pistolet", vova, null);
    private final Item kukla = new Item(1, "kukla", "vesch", false, masha, requestByValy);
    private final Item nosok = new Item(2, "nosok", "vesch", true, masha, null);
    private final Item pistol = new Item(3, "pistol", "oruzhie", true, valy, requestByVova);
    private final Item shlypa = new Item(4, "shlypa", "pistolet", true, vova, requestByValy);

    @Test
    void findByAvailableTrueAndNameContainingOrAvailableTrueAndDescriptionContainingIgnoreCase() {
        List<Item> items =
                repository.findByAvailableTrueAndNameContainingOrAvailableTrueAndDescriptionContainingIgnoreCase(
                        "vesch", "vesch");
        assertEquals(1, items.size());
        assertEquals(nosok, items.get(0));
    }

    @Test
    void findByAvailableTrueAndNameContainingOrAvailableTrueAndDescriptionContainingIgnoreCaseByPage() {
        List<Item> items =
                repository.findByAvailableTrueAndNameContainingOrAvailableTrueAndDescriptionContainingIgnoreCase(
                        "pist", "pist", PageRequest.of(0, 4, sort)).toList();
        assertEquals(2, items.size());
        assertEquals(pistol, items.get(0));
        assertEquals(shlypa, items.get(1));
    }

    @Test
    void findByOwnerId() {
        List<Item> items = repository.findByOwnerId(masha.getId());
        assertEquals(2, items.size());
        assertEquals(kukla, items.get(0));
        assertEquals(nosok, items.get(1));
    }

    @Test
    void findByOwner() {
        List<Item> items = repository.findByOwner(masha, PageRequest.of(1,1, sort)).toList();
        assertEquals(1, items.size());
        assertEquals(nosok, items.get(0));
    }

    @Test
    void findByRequestId() {
        List<Item> items = repository.findByRequestId(requestByValy.getId());
        assertEquals(2, items.size());
        assertEquals(kukla, items.get(0));
        assertEquals(shlypa, items.get(1));
    }
}