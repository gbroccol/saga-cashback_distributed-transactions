package ru.example.exception;

public class PurchaseDoesNotExistException extends RuntimeException {
    public PurchaseDoesNotExistException(String message) {
        super(message);
    }
}
