package ru.practicum.shareit.exception;

public class AllReadyChangedStatusException extends RuntimeException {
    public AllReadyChangedStatusException() {
        super("Статус был уже изменен.");
    }
}
