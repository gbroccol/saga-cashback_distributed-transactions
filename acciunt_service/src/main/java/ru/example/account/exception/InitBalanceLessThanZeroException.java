package ru.example.account.exception;

public class InitBalanceLessThanZeroException extends RuntimeException {
    public InitBalanceLessThanZeroException(String message) {
        super(message);
    }
}
