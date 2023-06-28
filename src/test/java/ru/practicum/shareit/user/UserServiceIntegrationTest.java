package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql("/test_add_users.sql")
@Transactional
class UserServiceIntegrationTest {
    @Autowired
    private UserService service;
    private final User masha = new User(2, "masha", "email@mail.ru");
    private final User vova = new User(3, "vova", "email1@mail.ru");
    private final User valy = new User(4, "valy", "demo@mail.ru");

    @Test
    @DirtiesContext
    void createUser() {
        User user = new User(null, "vasy", "sobaka@mail.ru");
        User createdUser = service.createUser(user);
        user.setId(createdUser.getId());
        assertEquals(user, createdUser);
    }

    @Test
    @DirtiesContext
    void updateUser() {
        User user = new User(masha.getId(), "vasy", null);
        User updatedUser = service.updateUser(user);
        user.setEmail(updatedUser.getEmail());
        assertEquals(user, updatedUser);
    }

    @Test
    @DirtiesContext
    void deleteUser() {
        service.deleteUser(masha.getId());
        List<User> users = service.getAllUsers();
        assertFalse(users.contains(masha));
    }

    @Test
    void getUser() {
        User user = service.getUser(vova.getId());
        assertEquals(vova, user);
    }

    @Test
    void getAllUsers() {
        List<User> users = service.getAllUsers();
        assertEquals(3, users.size());
        assertTrue(users.contains(masha) && users.contains(vova) && users.contains(valy));
    }
}