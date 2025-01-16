package ru.example.account.sender;

import ru.example.account.exception.SendDataToKafkaException;

public interface DataSender {
    void send(Long id, String value) throws SendDataToKafkaException;
}
