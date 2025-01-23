package ru.example.account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
public class DataSendingConfig {

    public final String topicPurchaseCreated;
    public final String topicPurchaseRejected;
    public final String topicPurchaseCanceled;

    public DataSendingConfig(@Value("${application.kafka.topic.purchase-created:purchase_created}") String topicPurchaseCreated,
                             @Value("${application.kafka.topic.purchase-rejected:purchase_rejected}") String topicPurchaseRejected,
                             @Value("${application.kafka.topic.purchase-canceled:purchase_canceled}") String topicPurchaseCanceled) {
        this.topicPurchaseCreated = topicPurchaseCreated;
        this.topicPurchaseRejected = topicPurchaseRejected;
        this.topicPurchaseCanceled = topicPurchaseCanceled;
    }
}