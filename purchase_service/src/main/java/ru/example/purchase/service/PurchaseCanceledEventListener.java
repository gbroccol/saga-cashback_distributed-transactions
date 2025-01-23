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
public class PurchaseCanceledEventListener {

    private static final Integer MAX_DELAY_MILLISECONDS = 5_000;

    private final ObjectMapper objectMapper;
    private final PurchaseService purchaseService;

    @KafkaListener(topics = "purchase_canceled", groupId = "purchase-group")
    public void handlePurchaseCanceled(String event) throws InterruptedException, JsonProcessingException {

        // todo может перезатереться на cancelling, зависит от порядки фиксации транзакций (можно добавить for update в select)
        // todo ловлю иногда ошибку - не отменяется начисление кешбека

        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);
        log.info("Обновляем статус покупки. Покупка отменена успешно accountId:{} purchaseId:{}",
                purchaseEvent.accountId(),
                purchaseEvent.purchaseId());

        simulateDelay();

        // обновить статус
        purchaseService.updateState(purchaseEvent.purchaseId(), PurchaseState.CANCELED);

        simulateDelay();
    }

    private void simulateDelay() throws InterruptedException {
        int delay = new Random().nextInt(MAX_DELAY_MILLISECONDS);
        log.info("Идет расчет в банке который хранит твою денежку ждать {} секунд", delay);
        Thread.sleep(delay);
    }
}
