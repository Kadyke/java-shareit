package ru.practicum.shareit.exception;

public class WrongParamsException extends RuntimeException {
    public WrongParamsException(String message) {
        super("Некорректные данные: " + message);
    }
}
