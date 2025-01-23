package ru.example.account.exception;

public class AccountDoesNotExistException extends Exception {
    public AccountDoesNotExistException(String message) {
        super(message);
    }
}
