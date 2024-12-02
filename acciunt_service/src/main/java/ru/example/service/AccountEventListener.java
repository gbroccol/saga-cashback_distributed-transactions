package ru.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.example.model.PurchaseEvent;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountEventListener {

    private final ObjectMapper objectMapper;
    private final AccountService accountService;


    @KafkaListener(topics = "purchase_create", groupId = "account-group")
    public void handlePurchaseCreated(String event) throws InterruptedException, JsonProcessingException {
        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);
        simulateDelay();
        System.out.println("___________");
        System.out.println(purchaseEvent.getAccountId());
        accountService.addCashBack(purchaseEvent.getAccountId(), purchaseEvent.getAmount());
        simulateDelay();
    }

    @KafkaListener(topics = "purchase_cancel", groupId = "account-group")
    public void handlePurchaseCanceled(String event) throws InterruptedException, JsonProcessingException {
        PurchaseEvent purchaseEvent = objectMapper.readValue(event, PurchaseEvent.class);
        simulateDelay();
        accountService.removeCashBack(purchaseEvent.getAccountId(), purchaseEvent.getAmount());
        simulateDelay();
    }


    private void simulateDelay() throws InterruptedException {
        int delay = new Random().nextInt(10_000); // do 10 sec
        System.out.println("Идет расчет в бухгалтерии который займет " + delay + "скунд");
        Thread.sleep(delay);
    }
}
