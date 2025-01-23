package ru.example.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.example.account.exception.AccountDoesNotExistException;
import ru.example.account.model.PurchaseEvent;

import java.util.Random;

@Log4j2
@Service
@RequiredArgsConstructor
public class PurchaseCreatedEventListener {

    private static final Integer MAX_DELAY_MILLISECONDS = 5_000;

    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "purchase_created", groupId = "account-group")
    public void handlePurchaseCreated(String event) throws InterruptedException, JsonProcessingException {

        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);

        simulateDelay();

        log.info("Обработка покупки. Начисляем кэшбэк за покупку accountId:{} purchaseId:{}",
                purchaseEvent.accountId(),
                purchaseEvent.purchaseId());

        try {
            // начислить кэшбэк, если покупка не отменена
            accountService.addCashBack(purchaseEvent.accountId(), purchaseEvent.amount());
        } catch (AccountDoesNotExistException e) {
            // todo закидывать в очередь и попробовать обработать позже
            log.error("can't create purchase - account does not exist");
            throw new RuntimeException(e);
        }

        simulateDelay();
    }

    private void simulateDelay() throws InterruptedException {
        int delay = new Random().nextInt(MAX_DELAY_MILLISECONDS);
        log.info("Идет расчет в бухгалтерии который займет {} секунд", delay);
        Thread.sleep(delay);
    }
}
