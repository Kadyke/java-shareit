package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService service;
    @Autowired
    private MockMvc mvc;
    private final User masha = new User(1, "masha", "email@mail.ru");
    private final User vova = new User(2, "vova", "email1@mail.ru");
    private final User valy = new User(3, "valy", "demo@mail.ru");

    @Test
    @SneakyThrows
    void createUser() {
        UserDto userDto = new UserDto(null, "vasy", "sobaka@mail.ru");
        when(service.createUser(UserMapper.toUser(userDto))).thenAnswer(invocationOnMock -> {
            User user = invocationOnMock.getArgument(0);
            user.setId(4);
            return user;
        });
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    @SneakyThrows
    void updateUser() {
        UserDto userDto = new UserDto(masha.getId(), null, "sobaka@mail.ru");
        when(service.updateUser(UserMapper.toUser(userDto))).thenAnswer(invocationOnMock -> {
            User user = invocationOnMock.getArgument(0);
            masha.update(user);
            return masha;
        });
        mvc.perform(patch("/users/{id}", masha.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(masha.getId())))
                .andExpect(jsonPath("$.name", is(masha.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    @SneakyThrows
    void deleteUser() {
        mvc.perform(delete("/users/{id}", masha.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getUser() {
        when(service.getUser(masha.getId())).thenReturn(masha);
        mvc.perform(get("/users/{id}", masha.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(masha.getId())))
                .andExpect(jsonPath("$.name", is(masha.getName())))
                .andExpect(jsonPath("$.email", is(masha.getEmail())));
    }

    @Test
    @SneakyThrows
    void getAllUsers() {
        when(service.getAllUsers()).thenReturn(List.of(masha, vova, valy));
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(masha.getId())))
                .andExpect(jsonPath("$[0].name", is(masha.getName())))
                .andExpect(jsonPath("$[0].email", is(masha.getEmail())))
                .andExpect(jsonPath("$[1].id", is(vova.getId())))
                .andExpect(jsonPath("$[1].name", is(vova.getName())))
                .andExpect(jsonPath("$[1].email", is(vova.getEmail())))
                .andExpect(jsonPath("$[2].id", is(valy.getId())))
                .andExpect(jsonPath("$[2].name", is(valy.getName())))
                .andExpect(jsonPath("$[2].email", is(valy.getEmail())));


    }
}