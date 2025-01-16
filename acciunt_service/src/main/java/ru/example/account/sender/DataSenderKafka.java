package ru.example.account.sender;

import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import ru.example.account.exception.SendDataToKafkaException;

import java.util.concurrent.ExecutionException;

@Log4j2
public class DataSenderKafka implements DataSender {

    private final String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public DataSenderKafka(String topic,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String msg) throws SendDataToKafkaException {
        try {
            log.info("sending msg (message:{}) to kafka...", msg);
            var result = kafkaTemplate.send(topic, msg).get();
            log.info("msg (message:{}) was sent to topic:{}, offset:{}", msg, topic, result.getRecordMetadata().offset());
        } catch (ExecutionException | InterruptedException ex) {
            log.error("error, msg (message:{}) was not sent to topic:{}", msg, topic, ex);
            throw new SendDataToKafkaException(ex.getMessage());
        }
    }
}
