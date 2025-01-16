package ru.example.account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.KafkaTemplate;
import ru.example.account.sender.DataSender;
import ru.example.account.sender.DataSenderKafka;

@Configuration
@PropertySource("classpath:application.yml")
public class ApplicationConfig {

    private final String topicPurchaseCreated;
    private final String topicPurchaseRejected;
    private final String topicPurchaseCanceled;

    public ApplicationConfig(@Value("${application.kafka.topic.purchase-created:purchase_created}") String topicPurchaseCreated,
                             @Value("${application.kafka.topic.purchase-rejected:purchase_rejected}") String topicPurchaseRejected,
                             @Value("${application.kafka.topic.purchase-canceled:purchase_canceled}") String topicPurchaseCanceled) {
        this.topicPurchaseCreated = topicPurchaseCreated;
        this.topicPurchaseRejected = topicPurchaseRejected;
        this.topicPurchaseCanceled = topicPurchaseCanceled;
    }

    @Bean
    public DataSender purchaseCreated(KafkaTemplate<String, String> kafkaTemplate) {
        return new DataSenderKafka(topicPurchaseCreated, kafkaTemplate);
    }

    @Bean
    public DataSender purchaseRejected(KafkaTemplate<String, String> kafkaTemplate) {
        return new DataSenderKafka(topicPurchaseRejected, kafkaTemplate);
    }

    @Bean
    public DataSender purchaseCanceled(KafkaTemplate<String, String> kafkaTemplate) {
        return new DataSenderKafka(topicPurchaseCanceled, kafkaTemplate);
    }

}
