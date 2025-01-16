package ru.example.exception;

public class PurchaseCanNotBeCanceledException extends RuntimeException {
    public PurchaseCanNotBeCanceledException(String message) {
        super(message);
    }
}
