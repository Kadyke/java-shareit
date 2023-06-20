package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(service.createUser(user));
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Integer id) {
        userDto.setId(id);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(service.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        service.deleteUser(id);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Integer id) {
        return UserMapper.toUserDto(service.getUser(id));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return UserMapper.collectionToUserDto(service.getAllUsers());
    }
}
