package ru.example.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.example.account.exception.AccountDoesNotExistException;
import ru.example.account.exception.NotEnoughMoneyException;
import ru.example.account.model.PurchaseEvent;
import ru.example.account.sender.DataSender;

import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PurchaseEventListener {

    private final ObjectMapper objectMapper;
    private final AccountService accountService;
    private final Map<String, DataSender> dataSenders;

    @KafkaListener(topics = "purchase_creating", groupId = "account-group")
    public void handlePurchaseCreating(String event) throws InterruptedException, JsonProcessingException {

        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);
        System.out.printf("--------> Обработка покупки. Вычитаем денежные средства за покупку accountId:%d purchaseId:%d  %n",
                purchaseEvent.getAccountId(),
                purchaseEvent.getPurchaseId());

        simulateDelay();

        try {
            // вычитаем сумму из баланса
            accountService.withdrawMoney(purchaseEvent.getAccountId(), purchaseEvent.getAmount());
        } catch (AccountDoesNotExistException | NotEnoughMoneyException e) {
            dataSenders.get("purchaseRejected").send(1L, objectMapper.writeValueAsString(purchaseEvent)); // todo переменные // todo 1L
            return;
        }

        // добавить событие в очередь успешных операций
        dataSenders.get("purchaseCreated").send(1L, objectMapper.writeValueAsString(purchaseEvent));

        simulateDelay();
    }

    @KafkaListener(topics = "purchase_created", groupId = "account-group")
    public void handlePurchaseCreated(String event) throws InterruptedException, JsonProcessingException {

        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);

        simulateDelay();

        System.out.printf("--------> Обработка покупки. Начисляем кэшбэк за покупку accountId:%d purchaseId:%d  %n",
                purchaseEvent.getAccountId(),
                purchaseEvent.getPurchaseId());

        try {
            // начислить кэшбэк, если покупка не отменена
            accountService.addCashBack(purchaseEvent.getAccountId(), purchaseEvent.getAmount());
        } catch (AccountDoesNotExistException e) {
            // todo log - можно закидывать в очередь и попробовать обработать позже
            throw new RuntimeException(e);
        }

        simulateDelay();
    }

    @KafkaListener(topics = "purchase_canceling", groupId = "account-group")
    public void handlePurchaseCanceled(String event) throws InterruptedException, JsonProcessingException {

        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);

        simulateDelay();

        System.out.printf("--------> Обработка отмены покупки. Возвращаем списанные средства и отменяем начисление кэшбэка accountId:%d purchaseId:%d %n",
                purchaseEvent.getAccountId(),
                purchaseEvent.getPurchaseId());

        try {
            // Возвращаем списанные средства и отменяем кэшбэк, если был начислен ранее
            accountService.cancelPurchase(purchaseEvent.getAccountId(), purchaseEvent.getAmount());
        } catch (AccountDoesNotExistException e) {
            // todo log - можно закидывать в очередь и попробовать обработать позже
            return;
        }

        // Добавить событие в очередь операций, которые были успешно отменены
        PurchaseEvent purchaseCancelEvent = new PurchaseEvent();
        purchaseCancelEvent.setAccountId(purchaseEvent.getAccountId());
        purchaseCancelEvent.setPurchaseId(purchaseEvent.getPurchaseId());
        purchaseCancelEvent.setAmount(purchaseEvent.getAmount());
        dataSenders.get("purchaseCanceled").send(1L, objectMapper.writeValueAsString(purchaseCancelEvent));

        simulateDelay();
    }

    private void simulateDelay() throws InterruptedException {
        int delay = new Random().nextInt(5_000); // do 5 sec
        System.out.println("Идет расчет в бухгалтерии который займет " + delay + "секунд");
        Thread.sleep(delay);
    }
}
