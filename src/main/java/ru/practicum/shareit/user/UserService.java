package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        userDto = UserMapper.toUserDto(repository.createUser(user));
        return userDto;
    }


    public UserDto updateUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        userDto = UserMapper.toUserDto(repository.updateUser(user));
        return userDto;
    }

    public void deleteUser(Integer id) {
        repository.deleteUser(id);
    }

    public UserDto getUser(Integer id) {
        UserDto userDto = UserMapper.toUserDto(repository.getUser(id));
        return userDto;
    }

    public List<UserDto> getAllUsers() {
        List<UserDto> users = new ArrayList<>();
        for (User user : repository.getAllUsers()) {
            users.add(UserMapper.toUserDto(user));
        }
        return users;
    }
}
