package ru.practicum.shareit.exception;

public class BookingForSelfException extends RuntimeException {
    public BookingForSelfException() {
        super("Нельзя бронировать собственные вещи.");
    }
}
