package ru.example.exception;

public class SendDataToKafkaException extends RuntimeException {
    public SendDataToKafkaException(String message) {
        super(message);
    }
}
