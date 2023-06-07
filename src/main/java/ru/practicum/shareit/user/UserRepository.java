package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

@Component
public class UserRepository {
    private final HashMap<Integer, User> users = new HashMap<>();
    private final HashSet<String> emails = new HashSet<>();
    private Integer id = 0;

    public User createUser(User user) {
        if (emails.contains(user.getEmail())) {
            throw new EmailException("C таким email пользователь уже создан.");
        }
        user.setId(++id);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    public User updateUser(User user) {
        if (emails.contains(user.getEmail()) && !Objects.equals(user.getEmail(), users.get(user.getId()).getEmail())) {
            throw new EmailException("C таким email пользователь уже создан.");
        }
        emails.remove(users.get(user.getId()).getEmail());
        users.get(user.getId()).update(user);
        emails.add(users.get(user.getId()).getEmail());
        return users.get(user.getId());
    }

    public void deleteUser(Integer id) {
        if (users.containsKey(id)) {
            emails.remove(users.get(id).getEmail());
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
