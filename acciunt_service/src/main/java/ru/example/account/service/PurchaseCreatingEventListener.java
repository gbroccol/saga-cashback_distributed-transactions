package ru.example.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.example.account.config.DataSendingConfig;
import ru.example.account.exception.AccountDoesNotExistException;
import ru.example.account.exception.NotEnoughMoneyException;
import ru.example.account.model.PurchaseEvent;
import ru.example.account.sender.DataSender;

import java.util.Random;

@Log4j2
@Service
@RequiredArgsConstructor
public class PurchaseCreatingEventListener {

    private static final Integer MAX_DELAY_MILLISECONDS = 5_000;

    private final AccountService accountService;
    private final ObjectMapper objectMapper;
    private final DataSender dataSender;
    private final DataSendingConfig dataSendingConfig;

    @KafkaListener(topics = "purchase_creating", groupId = "account-group")
    public void handlePurchaseCreating(String event) throws InterruptedException, JsonProcessingException {

        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);
        log.info("Обработка покупки. Вычитаем денежные средства за покупку accountId:{} purchaseId:{}",
                purchaseEvent.accountId(),
                purchaseEvent.purchaseId());

        simulateDelay();

        try {
            // вычитаем сумму из баланса
            accountService.withdrawMoney(purchaseEvent.accountId(), purchaseEvent.amount());
        } catch (AccountDoesNotExistException | NotEnoughMoneyException e) {
            dataSender.send(dataSendingConfig.topicPurchaseRejected, objectMapper.writeValueAsString(purchaseEvent));
            return;
        }

        // добавить событие в очередь успешных операций
        dataSender.send(dataSendingConfig.topicPurchaseCreated, objectMapper.writeValueAsString(purchaseEvent));

        simulateDelay();
    }

    private void simulateDelay() throws InterruptedException {
        int delay = new Random().nextInt(MAX_DELAY_MILLISECONDS);
        log.info("Идет расчет в бухгалтерии который займет {} секунд", delay);
        Thread.sleep(delay);
    }
}
