package ru.example.purchase.exception;

public class PurchaseCanNotBeCanceledException extends RuntimeException {
    public PurchaseCanNotBeCanceledException(String message) {
        super(message);
    }
}
