package ru.example.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import ru.example.sender.DataSender;
import ru.example.sender.DataSenderKafka;


@Configuration
public class ApplicationConfig {

    private static final String topicPurchaseCreated = "purchase-created"; // todo тут оставить (@Value) или выносить в yml?
    private static final String topicPurchaseCanceled = "purchase-canceled"; // todo - || -

    @Bean
    NewTopic purchaseCreatedTopic() {
        return TopicBuilder.name(topicPurchaseCreated)
                .partitions(2)
                .replicas(2)
                .build();
    }

    @Bean
    NewTopic purchaseCanceledTopic() {
        return TopicBuilder.name(topicPurchaseCanceled)
                .partitions(2)
                .replicas(2)
                .build();
    }

    @Bean
    public DataSender purchaseCreated(KafkaTemplate<String, String> kafkaTemplate) {
        return new DataSenderKafka(topicPurchaseCreated, kafkaTemplate);
    }

    @Bean
    public DataSender purchaseCanceled(KafkaTemplate<String, String> kafkaTemplate) {
        return new DataSenderKafka(topicPurchaseCanceled, kafkaTemplate);
    }
}
