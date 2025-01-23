package ru.example.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.example.account.config.DataSendingConfig;
import ru.example.account.exception.AccountDoesNotExistException;
import ru.example.account.model.PurchaseEvent;
import ru.example.account.sender.DataSender;

import java.util.Random;

@Log4j2
@Service
@RequiredArgsConstructor
public class PurchaseCancelingEventListener {

    private static final Integer MAX_DELAY_MILLISECONDS = 5_000;

    private final AccountService accountService;
    private final ObjectMapper objectMapper;
    private final DataSender dataSender;
    private final DataSendingConfig dataSendingConfig;

    @KafkaListener(topics = "purchase_canceling", groupId = "account-group")
    public void handlePurchaseCanceled(String event) throws InterruptedException, JsonProcessingException {

        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);

        simulateDelay();

        log.info("Обработка отмены покупки. Возвращаем списанные средства и отменяем начисление кэшбэка accountId:{} purchaseId:{}",
                purchaseEvent.accountId(),
                purchaseEvent.purchaseId());

        try {
            // Возвращаем списанные средства и отменяем кэшбэк, если был начислен ранее
            accountService.cancelPurchase(purchaseEvent.accountId(), purchaseEvent.amount());
        } catch (AccountDoesNotExistException e) {
            // todo закидывать в очередь и попробовать обработать позже
            log.error("can't cancel purchase - account does not exist accountId:{}", purchaseEvent.accountId());

            return;
        }

        // Добавить событие в очередь операций, которые были успешно отменены
        dataSender.send(dataSendingConfig.topicPurchaseCanceled, objectMapper.writeValueAsString(purchaseEvent));

        simulateDelay();
    }

    private void simulateDelay() throws InterruptedException {
        int delay = new Random().nextInt(MAX_DELAY_MILLISECONDS);
        log.info("Идет расчет в бухгалтерии который займет {} секунд", delay);
        Thread.sleep(delay);
    }

}
