package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

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
        User oldUser = repository.getReferenceById(user.getId());
        oldUser.update(user);
        repository.save(oldUser);
        return oldUser;
    }

    public void deleteUser(Integer id) {
        repository.deleteById(id);
    }

    public User getUser(Integer id) {
        return repository.getReferenceById(id);
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }
}
