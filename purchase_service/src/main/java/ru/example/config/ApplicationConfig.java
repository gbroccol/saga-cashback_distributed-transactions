package ru.example.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import ru.example.sender.DataSender;
import ru.example.sender.DataSenderKafka;


@Configuration
@PropertySource("classpath:application.yml")
public class ApplicationConfig {

    private final String topicPurchaseCreating;
    private final String topicPurchaseCanceling;

    public ApplicationConfig(@Value("${application.kafka.topic.purchase-creating:purchase_creating}") String topicPurchaseCreating,
                             @Value("${application.kafka.topic.purchase-canceling:purchase_canceling}") String topicPurchaseCanceling) {
        this.topicPurchaseCreating = topicPurchaseCreating;
        this.topicPurchaseCanceling = topicPurchaseCanceling;
    }

    @Bean
    NewTopic purchaseCreatedTopic() {
        return TopicBuilder.name(topicPurchaseCreating)
                .partitions(2)
                .replicas(2)
                .build();
    }

    @Bean
    NewTopic purchaseCanceledTopic() {
        return TopicBuilder.name(topicPurchaseCanceling)
                .partitions(2)
                .replicas(2)
                .build();
    }

    @Bean
    public DataSender purchaseCreating(KafkaTemplate<String, String> kafkaTemplate) {
        return new DataSenderKafka(topicPurchaseCreating, kafkaTemplate);
    }

    @Bean
    public DataSender purchaseCanceling(KafkaTemplate<String, String> kafkaTemplate) {
        return new DataSenderKafka(topicPurchaseCanceling, kafkaTemplate);
    }

}
