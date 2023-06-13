package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Integer id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Integer ownerId;
    private ItemRequest request;

    public Item(ItemDto item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.available = item.getAvailable();
    }

    public void update(Item item) {
        if (item.name != null) {
            this.name = item.name;
        }
        if (item.description != null) {
            this.description = item.description;
        }
        if (item.available != null) {
            this.available = item.available;
        }
        if (item.ownerId != null) {
            this.ownerId = item.ownerId;
        }
        if (item.request != null) {
            this.request = item.request;
        }
    }
}
