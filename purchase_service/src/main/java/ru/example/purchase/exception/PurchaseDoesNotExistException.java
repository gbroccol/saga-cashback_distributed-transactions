package ru.example.purchase.exception;

public class PurchaseDoesNotExistException extends RuntimeException {
    public PurchaseDoesNotExistException(String message) {
        super(message);
    }
}
