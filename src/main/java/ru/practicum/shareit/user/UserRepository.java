package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

@Component
public class UserRepository {
    private final HashMap<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    public User createUser(User user) {
        if (users.containsValue(user)) {
            throw new EmailException("C таким email пользователь уже создан.");
        }
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        if (users.containsValue(user) && !Objects.equals(user.getEmail(), users.get(user.getId()).getEmail())) {
            throw new EmailException("C таким email пользователь уже создан.");
        }
        users.get(user.getId()).update(user);
        return users.get(user.getId());
    }

    public void deleteUser(Integer id) {
        if (users.containsKey(id)) {
            users.remove(id);
            return;
        }
        throw new NotFoundException();
    }

    public User getUser(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new NotFoundException();
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }
}
