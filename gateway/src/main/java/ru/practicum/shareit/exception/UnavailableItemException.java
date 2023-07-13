package ru.practicum.shareit.exception;

public class UnavailableItemException extends RuntimeException {
    public UnavailableItemException() {
        super("Данная вещь уже занята.");
    }
}
