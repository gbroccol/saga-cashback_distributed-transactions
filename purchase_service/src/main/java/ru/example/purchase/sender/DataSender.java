package ru.example.purchase.sender;

import ru.example.purchase.exception.SendDataToKafkaException;

public interface DataSender {
    void send(String value) throws SendDataToKafkaException;
}
