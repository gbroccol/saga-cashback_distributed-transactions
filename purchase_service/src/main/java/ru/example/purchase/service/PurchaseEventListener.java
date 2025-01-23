package ru.example.purchase.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.example.purchase.model.PurchaseEvent;
import ru.example.purchase.model.PurchaseState;

import java.util.Random;

@Log4j2
@Service
@RequiredArgsConstructor
public class PurchaseEventListener {

    private final ObjectMapper objectMapper;
    private final PurchaseService purchaseService;

    @KafkaListener(topics = "purchase_created", groupId = "purchase-group")
    public void handlePurchaseCreated(String event) throws InterruptedException, JsonProcessingException {

        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);
        log.info("Обновляем статус покупки. Покупка прошла успешно accountId:{} purchaseId:{}",
                purchaseEvent.accountId(),
                purchaseEvent.purchaseId());

        simulateDelay();

        // обновить статус
        purchaseService.setState(purchaseEvent.purchaseId(), PurchaseState.CREATED);

        simulateDelay();
    }

    @KafkaListener(topics = "purchase_rejected", groupId = "purchase-group")
    public void handlePurchaseRejected(String event) throws InterruptedException, JsonProcessingException {

        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);
        log.info("Обновляем статус покупки. Покупка отклонена. Аккаунт не существует или на нем не достаточно средств accountId:{} purchaseId:{}",
                purchaseEvent.accountId(),
                purchaseEvent.purchaseId());

        simulateDelay();

        // обновить статус
        purchaseService.setState(purchaseEvent.purchaseId(), PurchaseState.REJECTED);

        simulateDelay();
    }

    @KafkaListener(topics = "purchase_canceled", groupId = "purchase-group")
    public void handlePurchaseCanceled(String event) throws InterruptedException, JsonProcessingException {

        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);
        log.info("Обновляем статус покупки. Покупка отменена успешно accountId:{} purchaseId:{}",
                purchaseEvent.accountId(),
                purchaseEvent.purchaseId());

        simulateDelay();

        // обновить статус
        purchaseService.setState(purchaseEvent.purchaseId(), PurchaseState.CANCELED);

        simulateDelay();
    }

    private void simulateDelay() throws InterruptedException {
        int delay = new Random().nextInt(5_000); // do 5 sec
        System.out.println("Идет расчет в бухгалтерии который займет " + delay + "секунд");
        Thread.sleep(delay);
    }
}
