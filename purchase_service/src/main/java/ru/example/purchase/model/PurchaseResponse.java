package ru.example.purchase.model;

public record PurchaseResponse(Long purchaseId,
                               PurchaseState state) {

}