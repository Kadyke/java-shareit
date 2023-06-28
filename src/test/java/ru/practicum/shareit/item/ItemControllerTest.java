package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOut;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    ItemService service;
    @Autowired
    MockMvc mvc;
    private User masha;
    private Item kukla;

    @BeforeEach
    void getArguments() {
        masha = new User(1, "masha", "email@mail.ru");
        kukla = new Item(1, "kukla", "igrushka", true, masha, null);
    }

    @Test
    @SneakyThrows
    void addNewItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("klubok");
        itemDto.setDescription("nitki");
        itemDto.setAvailable(true);
        when(service.addNewItem(itemDto, masha.getId())).thenAnswer(invocationOnMock -> {
            Item item = ItemMapper.toItem(invocationOnMock.getArgument(0));
            item.setId(1);
            item.setOwner(masha);
            return ItemMapper.toItemDto(item);
        });
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", masha.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    void updateItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("klubok");
        itemDto.setId(kukla.getId());
        when(service.updateItem(itemDto, masha.getId())).thenAnswer(invocationOnMock -> {
            Item item = ItemMapper.toItem(invocationOnMock.getArgument(0));
            kukla.update(item);
            return kukla;
        });
        mvc.perform(patch("/items/{id}", itemDto.getId())
                        .header("X-Sharer-User-Id", masha.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(kukla.getDescription())))
                .andExpect(jsonPath("$.available", is(kukla.getAvailable())));
    }

    @Test
    @SneakyThrows
    void getItem() {
        when(service.getItem(kukla.getId(), masha.getId())).thenAnswer(invocationOnMock -> new ItemOut(kukla.getId(),
                kukla.getName(), kukla.getDescription(), kukla.getAvailable(), null, null,
                new ArrayList<>()));
        mvc.perform(get("/items/{id}", kukla.getId())
                        .header("X-Sharer-User-Id", masha.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(kukla.getId())))
                .andExpect(jsonPath("$.name", is(kukla.getName())))
                .andExpect(jsonPath("$.description", is(kukla.getDescription())))
                .andExpect(jsonPath("$.available", is(kukla.getAvailable())));
    }

    @Test
    @SneakyThrows
    void getAllUsersItems() {
        when(service.getAllUsersItems(masha.getId())).thenAnswer(invocationOnMock -> List.of(new ItemOut(kukla.getId(),
                kukla.getName(), kukla.getDescription(), kukla.getAvailable(), null, null,
                new ArrayList<>())));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", masha.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(kukla.getId())))
                .andExpect(jsonPath("$[0].name", is(kukla.getName())))
                .andExpect(jsonPath("$[0].description", is(kukla.getDescription())))
                .andExpect(jsonPath("$[0].available", is(kukla.getAvailable())));
    }

    @Test
    @SneakyThrows
    void search() {
        when(service.search("kukla", 0, 2)).thenAnswer(invocationOnMock -> List.of(kukla));
        mvc.perform(get("/items/search")
                        .param("text", "kukla")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(kukla.getId())))
                .andExpect(jsonPath("$[0].name", is(kukla.getName())))
                .andExpect(jsonPath("$[0].description", is(kukla.getDescription())))
                .andExpect(jsonPath("$[0].available", is(kukla.getAvailable())));
    }

    @Test
    @SneakyThrows
    void addNewComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Chet ne och");
        LocalDateTime now = LocalDateTime.now();
        when(service.addNewComment(commentDto, kukla.getId(), masha.getId())).thenAnswer(invocationOnMock -> {
            Comment comment = CommentMapper.toComment(invocationOnMock.getArgument(0));
            comment.setItem(kukla);
            comment.setAuthor(masha);
            comment.setCreatedTime(now);
            comment.setId(1);
            return CommentMapper.toCommentDto(comment);
        });
        mvc.perform(post("/items/{id}/comment", kukla.getId())
                        .header("X-Sharer-User-Id", masha.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(masha.getName())));
    }
}