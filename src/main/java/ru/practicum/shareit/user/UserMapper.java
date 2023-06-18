package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(user);
    }

    public User toUser(UserDto user) {
        return new User(user);
    }

    public List<UserDto> collectionToUserDto(Collection<User> users) {
        return users.stream().map(UserMapper::toUserDto).collect(toList());
    }
}
