package ru.example.sender;

import ru.example.exception.SendDataToKafkaException;

public interface DataSender {
    void send(Long id, String value) throws SendDataToKafkaException;
}
