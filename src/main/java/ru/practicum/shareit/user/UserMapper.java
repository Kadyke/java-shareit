package ru.practicum.shareit.user;


import ru.practicum.shareit.user.dto.UserDto;


public class UserMapper {
    private UserMapper() {
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user);
    }

    public static User toUser(UserDto user) {
        return new User(user);
    }

}
