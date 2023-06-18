package ru.practicum.shareit.exception;

public class CommentWithoutBookingException extends RuntimeException {
    public CommentWithoutBookingException() {
        super("Нельзя комментировать неарендованные вещи.");
    }
}
