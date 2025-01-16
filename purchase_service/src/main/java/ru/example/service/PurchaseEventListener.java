package ru.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.example.model.PurchaseEvent;
import ru.example.model.PurchaseState;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class PurchaseEventListener {

    private final ObjectMapper objectMapper;
    private final PurchaseService purchaseService;

    // todo @Transactional
    @KafkaListener(topics = "purchase_created", groupId = "purchase-group")
    public void handlePurchaseCreated(String event) throws InterruptedException, JsonProcessingException {

        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);
        System.out.printf("--------> Обновляем статус покупки. Покупка прошла успешно accountId:%d purchaseId:%d  %n",
                purchaseEvent.getAccountId(),
                purchaseEvent.getPurchaseId());

        simulateDelay();

        // обновить статус
        purchaseService.setState(purchaseEvent.getPurchaseId(), PurchaseState.CREATED);

        simulateDelay();
    }

    @KafkaListener(topics = "purchase_rejected", groupId = "purchase-group")
    public void handlePurchaseRejected(String event) throws InterruptedException, JsonProcessingException {

        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);
        System.out.printf("--------> Обновляем статус покупки. Покупка отменена успешно accountId:%d purchaseId:%d  %n",
                purchaseEvent.getAccountId(),
                purchaseEvent.getPurchaseId());

        simulateDelay();

        // обновить статус
        purchaseService.setState(purchaseEvent.getPurchaseId(), PurchaseState.REJECTED);

        simulateDelay();
    }

    @KafkaListener(topics = "purchase_canceled", groupId = "purchase-group")
    public void handlePurchaseCanceled(String event) throws InterruptedException, JsonProcessingException {

        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);
        System.out.printf("--------> Обновляем статус покупки. Покупка отменена успешно accountId:%d purchaseId:%d  %n",
                purchaseEvent.getAccountId(),
                purchaseEvent.getPurchaseId());

        simulateDelay();

        // обновить статус
        purchaseService.setState(purchaseEvent.getPurchaseId(), PurchaseState.CANCELED);

        simulateDelay();
    }

    private void simulateDelay() throws InterruptedException {
        int delay = new Random().nextInt(5_000); // do 5 sec
        System.out.println("Идет расчет в бухгалтерии который займет " + delay + "секунд");
        Thread.sleep(delay);
    }
}
