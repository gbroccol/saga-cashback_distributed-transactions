package ru.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.model.PurchaseRequest;
import ru.example.model.PurchaseResponse;
import ru.example.service.PurchaseService;

@RestController
@RequestMapping("/api/v1/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> getPurchase(@PathVariable Long id) {
        return new ResponseEntity<>(purchaseService.get(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PurchaseResponse> createPurchase(@RequestBody PurchaseRequest request) {
        try {
            return new ResponseEntity<>(purchaseService.create(request), HttpStatus.CREATED);
        } catch (JsonProcessingException | InterruptedException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PurchaseResponse> cancelPurchase(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(purchaseService.cancel(id), HttpStatus.OK);
        } catch (JsonProcessingException | InterruptedException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
