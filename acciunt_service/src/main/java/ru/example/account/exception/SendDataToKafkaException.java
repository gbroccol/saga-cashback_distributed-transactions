package ru.example.account.exception;

public class SendDataToKafkaException extends RuntimeException {
    public SendDataToKafkaException(String message) {
        super(message);
    }
}
