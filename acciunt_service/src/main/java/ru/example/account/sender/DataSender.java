package ru.example.account.sender;

import ru.example.account.exception.SendDataToKafkaException;

public interface DataSender {
    void send(String topic, String value) throws SendDataToKafkaException;
}
