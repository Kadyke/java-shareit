package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService service;
    @Autowired
    private MockMvc mvc;
    private final LocalDateTime now = LocalDateTime.now();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final User vova = new User(2, "vova", "email1@mail.ru");
    private final User valy = new User(3, "valy", "demo@mail.ru");
    private final ItemRequest requestByValy = new ItemRequest(1, "hochu igrushku dly devochki", valy, now);
    private final ItemRequest requestByVova = new ItemRequest(2, "hochu pistolet", vova, now.plusHours(1));

    @Test
    @SneakyThrows
    void addNewRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("hochem");
        when(service.addNewRequest(Mockito.any(ItemRequestDto.class))).thenAnswer(invocationOnMock -> {
            ItemRequest request = ItemRequestMapper.toItemRequest(invocationOnMock.getArgument(0), vova);
            request.setId(3);
            request.setCreatedTime(now);
            return ItemRequestMapper.toItemRequestDto(request);
        });
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", vova.getId())
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.requesterId", is(vova.getId())))
                .andExpect(jsonPath("$.created", is(now.format(formatter))));

    }

    @Test
    @SneakyThrows
    void getUserRequests() {
        when(service.getUserRequests(valy.getId())).thenReturn(ItemRequestMapper.collectionToItemRequestDto(
                List.of(requestByValy)));
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", valy.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestByValy.getId())))
                .andExpect(jsonPath("$[0].description", is(requestByValy.getDescription())))
                .andExpect(jsonPath("$[0].requesterId", is(requestByValy.getRequester().getId())))
                .andExpect(jsonPath("$[0].created", is(requestByValy.getCreatedTime().format(formatter))));
    }

    @Test
    @SneakyThrows
    void getAllRequests() {
        when(service.getAllRequests(valy.getId())).thenReturn(ItemRequestMapper.collectionToItemRequestDto(
                List.of(requestByVova)));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", valy.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestByVova.getId())))
                .andExpect(jsonPath("$[0].description", is(requestByVova.getDescription())))
                .andExpect(jsonPath("$[0].requesterId", is(requestByVova.getRequester().getId())))
                .andExpect(jsonPath("$[0].created", is(requestByVova.getCreatedTime().format(formatter))));
    }

    @Test
    @SneakyThrows
    void getRequest() {
        when(service.getRequest(valy.getId(), requestByValy.getId())).thenReturn(ItemRequestMapper.toItemRequestDto(
                requestByValy));
        mvc.perform(get("/requests/{id}", requestByValy.getId())
                        .header("X-Sharer-User-Id", valy.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestByValy.getId())))
                .andExpect(jsonPath("$.description", is(requestByValy.getDescription())))
                .andExpect(jsonPath("$.requesterId", is(requestByValy.getRequester().getId())))
                .andExpect(jsonPath("$.created", is(requestByValy.getCreatedTime().format(formatter))));
    }
}