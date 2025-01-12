package ru.example.sender;

import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;

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
    public void send(StringValue msg) {
        try {
            log.info("sending msg (message id:{}) to kafka...", msg.id());
            var result = kafkaTemplate.send(topic, msg.message()).get();
            log.info("msg (message id:{}) was sent to topic:{}, offset:{}", msg.id(), topic, result.getRecordMetadata().offset());
        } catch (Exception ex) {
            log.error("error, msg (message id:{}) was not sent to topic:{}", msg.id(), topic, ex);
        }

    }
}
