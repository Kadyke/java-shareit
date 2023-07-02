package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build()
        );
    }

    public ResponseEntity<Object> getById(Integer id, Integer userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> create(ItemDto itemDto, Integer userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(ItemDto itemDto, Integer userId) {
        return patch("/" + itemDto.getId(), userId, itemDto);
    }

    public ResponseEntity<Object> getAllByUser(Integer userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllByUser(Integer userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("from", from, "size", size);
        return get("?from={from}&size={size}", Long.valueOf(userId), parameters);
    }

    public ResponseEntity<Object> search(String word) {
        Map<String, Object> parameters = Map.of("text", word);
        return get("/search?text={text}", null, parameters);
    }

    public ResponseEntity<Object> search(String word, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("text", word, "from", from, "size", size);
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> addComment(CommentDto commentDto, Integer itemId, Integer userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
