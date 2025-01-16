package ru.example.purchase.exception;

public class SendDataToKafkaException extends RuntimeException {
    public SendDataToKafkaException(String message) { super(message); }
}
