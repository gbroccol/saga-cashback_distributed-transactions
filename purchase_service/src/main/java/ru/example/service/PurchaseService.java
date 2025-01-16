package ru.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.exception.PurchaseCanNotBeCanceledException;
import ru.example.exception.PurchaseDoesNotExistException;
import ru.example.exception.SendDataToKafkaException;
import ru.example.model.*;
import ru.example.repository.PurchaseRepository;
import ru.example.sender.DataSender;

import java.util.Map;
import java.util.Random;

@Log4j2
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final ObjectMapper objectMapper;
    private final PurchaseRepository purchaseRepository;
    private final Map<String, DataSender> dataSenders;

    @Transactional
    public PurchaseResponse create(PurchaseRequest purchase) throws SendDataToKafkaException, JsonProcessingException, InterruptedException {

        simulateDelay();

        Purchase purchaseEntity = new Purchase();
        purchaseEntity.setAmount(purchase.getAmount());
        purchaseEntity.setProductName(purchase.getProductName());
        purchaseEntity.setState(PurchaseState.CREATING); // todo проверить работу на БД
        purchaseEntity.setAccountId(purchase.getAccountId());

        Purchase save = purchaseRepository.save(purchaseEntity);

        simulateDelay();

        // todo
//        if (ThreadLocalRandom.current().nextInt(1, 4) == 3) {
//            log.info("Random error - error while sending msg (message id:{}) to kafka...", save.getId());
//            throw new SendDataToKafkaException("error while sending data to kafka");
//        }

        PurchaseEvent event = new PurchaseEvent();
        event.setAccountId(save.getAccountId());
        event.setPurchaseId(save.getId());
        event.setAmount(save.getAmount());

        dataSenders.get("purchaseCreating").send(save.getId(), objectMapper.writeValueAsString(event));

        simulateDelay();
        return new PurchaseResponse(save.getId(), save.getState());
    }

    @Transactional
    public PurchaseResponse cancel(Long id) throws JsonProcessingException, InterruptedException {

        simulateDelay();

        Purchase purchase = purchaseRepository.findById(id).orElseThrow(
                () -> new PurchaseDoesNotExistException("Покупка не найдена"));

        if (purchase.getState() == PurchaseState.CANCELING || purchase.getState() == PurchaseState.CANCELED) {
            return new PurchaseResponse(purchase.getId(), purchase.getState());
        } else if (purchase.getState() != PurchaseState.CREATED) {
            throw new PurchaseCanNotBeCanceledException("Операция может быть отменена только если находится в статусе CREATED");
        }

        purchase.setState(PurchaseState.CANCELING);
        purchase = purchaseRepository.save(purchase);

        PurchaseEvent event = new PurchaseEvent();
        event.setAccountId(purchase.getAccountId());
        event.setPurchaseId(purchase.getId());
        event.setAmount(purchase.getAmount());

        simulateDelay();

        dataSenders.get("purchaseCanceling").send(purchase.getId(), objectMapper.writeValueAsString(event));
        return new PurchaseResponse(purchase.getId(), purchase.getState());
    }

    public PurchaseResponse get(Long id) {

        Purchase purchase = purchaseRepository.findById(id).orElseThrow(
                () -> new PurchaseDoesNotExistException("Покупка не найдена"));

        return new PurchaseResponse(purchase.getId(), purchase.getState());
    }

    public void setState(Long id, PurchaseState purchaseState) {
        Purchase purchase = purchaseRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Покупка не найдена"));
        purchase.setState(purchaseState);
        purchaseRepository.save(purchase);
    }

    private void simulateDelay() throws InterruptedException {
        int delay = new Random().nextInt(5_000); // do 5 sec
        System.out.println("Идет расчет в банке который хранит твою денежку ждать " + delay + "секунд");
        Thread.sleep(delay);
    }
}
