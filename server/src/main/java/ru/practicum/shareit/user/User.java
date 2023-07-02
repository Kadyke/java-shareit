package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Column(unique = true)
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
}

