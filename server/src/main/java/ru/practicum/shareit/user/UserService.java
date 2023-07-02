package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User createUser(User user) {
        return repository.save(user);
    }

    public User updateUser(User user) {
        User oldUser = repository.findById(user.getId()).orElseThrow(NotFoundException::new);
        oldUser.update(user);
        repository.save(oldUser);
        return oldUser;
    }

    public void deleteUser(Integer id) {
        repository.findById(id).orElseThrow(NotFoundException::new);
        repository.deleteById(id);
    }

    public User getUser(Integer id) {
        return repository.findById(id).orElseThrow(NotFoundException::new);
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }
}
