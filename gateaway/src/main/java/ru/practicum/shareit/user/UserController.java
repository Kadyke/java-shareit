package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient client;

    public UserController(UserClient client) {
        this.client = client;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        return client.create(userDto);
    }

//    @PatchMapping("/{id}")
//    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Integer id) {
//        userDto.setId(id);
//        User user = UserMapper.toUser(userDto);
//        return UserMapper.toUserDto(client.updateUser(user));
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteUser(@PathVariable Integer id) {
//        client.deleteUser(id);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Integer id) {
        return client.getById(id);
    }

//    @GetMapping
//    public List<UserDto> getAllUsers() {
//        return UserMapper.collectionToUserDto(client.getAllUsers());
//    }
}
