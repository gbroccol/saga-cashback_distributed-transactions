package ru.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.exception.SendDataToKafkaException;
import ru.example.model.Purchase;
import ru.example.model.PurchaseEvent;
import ru.example.model.PurchaseRequest;
import ru.example.repository.PurchaseRepository;
import ru.example.sender.DataSender;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Log4j2
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final ObjectMapper objectMapper;
    private final PurchaseRepository purchaseRepository;
    private final AccountMoneyOperations  accountService;
    private final Map<String, DataSender> dataSenders;

    @Transactional
    public void create(PurchaseRequest purchase) throws SendDataToKafkaException, JsonProcessingException, InterruptedException {

        simulateDelay();

        accountService.withdrawMoney(purchase.getAccountId(), purchase.getAmount());

        Purchase purchaseEntity = new Purchase(); // todo заменить на map struct
        purchaseEntity.setAmount(purchase.getAmount());
        purchaseEntity.setProductName(purchase.getProductName());
        purchaseEntity.setCanceled(false);
        purchaseEntity.setAccountId(purchase.getAccountId());
        Purchase save = purchaseRepository.save(purchaseEntity);

        simulateDelay();

        if (ThreadLocalRandom.current().nextInt(1, 4) == 3) {
            log.info("Random error - error while sending msg (message id:{}) to kafka...", save.getId());
            throw new SendDataToKafkaException("error while sending data to kafka");
        }

        PurchaseEvent event = new PurchaseEvent();
        event.setAccountId(save.getAccountId());
        event.setAmount(save.getAmount());

        dataSenders.get("purchaseCreated").send(save.getId(), objectMapper.writeValueAsString(event));

        simulateDelay();
    }

    public void cancel(Long id) throws JsonProcessingException, InterruptedException {

        simulateDelay();

        Purchase canceledPurchase = purchaseRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Покупка не найдена"));

        accountService.addMoney(canceledPurchase.getAccountId(), canceledPurchase.getAmount());

        if (canceledPurchase.isCanceled()) {
            throw new RuntimeException("Покупка уже отменена ожидайте возврата средств");
        }
        canceledPurchase.setCanceled(true);
        Purchase save = purchaseRepository.save(canceledPurchase);

        PurchaseEvent event = new PurchaseEvent();
        event.setAccountId(save.getId());
        event.setAmount(save.getAmount());

        simulateDelay();

        dataSenders.get("purchaseCanceled").send(save.getId(), objectMapper.writeValueAsString(event));
    }

    private void simulateDelay() throws InterruptedException {
        int delay = new Random().nextInt(10_000); // do 10 sec
        System.out.println("Идет расчет в банке который хранит твою денежку ждать " + delay + "секунд");
        Thread.sleep(delay);
    }
}
