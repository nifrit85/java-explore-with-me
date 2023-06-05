package ru.practicum.exception;

public class NotFound extends RuntimeException {
    public NotFound(String type, Long id) {
        super(type + " with id=" + id + " was not found");
    }
}
