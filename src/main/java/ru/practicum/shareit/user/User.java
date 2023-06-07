package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Email;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private Integer id;
    private String name;
    @Email
    private String email;

    public User(UserDto user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }

    public void update(User user) {
        if (user.name != null) {
            this.name = user.name;
        }
        if (user.email != null) {
            this.email = user.email;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(this.email, user.email);
    }
}

