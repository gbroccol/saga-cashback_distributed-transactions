package ru.example.purchase.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
public class DataSendingConfig {

    public final String topicPurchaseCreating;
    public final String topicPurchaseCanceling;

    public DataSendingConfig(@Value("${application.kafka.topic.purchase-created:purchase_creating}") String topicPurchaseCreating,
                             @Value("${application.kafka.topic.purchase-canceled:purchase_canceling}") String topicPurchaseCanceling) {
        this.topicPurchaseCreating = topicPurchaseCreating;
        this.topicPurchaseCanceling = topicPurchaseCanceling;
    }

}
