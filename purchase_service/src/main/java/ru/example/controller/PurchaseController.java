package ru.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.model.PurchaseRequest;
import ru.example.service.PurchaseService;

@RestController
@RequestMapping("/api/v1/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<Void> createPurchase(@RequestBody PurchaseRequest request) {
        try {
            purchaseService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (JsonProcessingException | InterruptedException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelPurchase(@PathVariable Long id) {
        try {
            purchaseService.cancel(id);
            return ResponseEntity.ok().build();
        } catch (JsonProcessingException | InterruptedException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
