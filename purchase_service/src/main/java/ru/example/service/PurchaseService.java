package ru.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.example.model.Purchase;
import ru.example.model.PurchaseEvent;
import ru.example.model.PurchaseRequest;
import ru.example.repo.PurchaseRepository;

import java.util.Random;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final ObjectMapper objectMapper;

    private final PurchaseRepository purchaseRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public Purchase createPurchase(PurchaseRequest purchase) throws InterruptedException, JsonProcessingException, ExecutionException {
        simulateDelay();

        Purchase purchaseEntity = new Purchase();
        purchaseEntity.setAmount(purchase.getAmount());
        purchaseEntity.setProductName(purchase.getProductName());
        purchaseEntity.setCanceled(false);
        purchaseEntity.setAccountId(purchase.getAccountId());
        Purchase save = purchaseRepository.save(purchaseEntity);

        simulateDelay();
        PurchaseEvent event = new PurchaseEvent();
        event.setAccountId(save.getId());
        event.setAmount(save.getAmount());

        kafkaTemplate.send("purchase_create", objectMapper.writeValueAsString(event)).get();
        simulateDelay();

        return save;
    }

    public void cancel(Long id) throws InterruptedException, JsonProcessingException, ExecutionException {
        simulateDelay();

        Purchase purchaseCancel = purchaseRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Покупка не найдена")
        );

        if (purchaseCancel.isCanceled()) {
            throw new RuntimeException("Покупка уже отменена ожидайте возврата средств");
        }
        purchaseCancel.setCanceled(true);
        Purchase save = purchaseRepository.save(purchaseCancel);


        PurchaseEvent event = new PurchaseEvent();
        event.setAccountId(save.getId());
        event.setAmount(save.getAmount());

        simulateDelay();

        kafkaTemplate.send("purchase_cancel", objectMapper.writeValueAsString(event)).get();


    }

    private void simulateDelay() throws InterruptedException {
        int delay = new Random().nextInt(10_000); // do 10 sec
        System.out.println("Идет расчет в банке который хранит твою денежку ждать " + delay + "скунд");
        Thread.sleep(delay);
    }
}
