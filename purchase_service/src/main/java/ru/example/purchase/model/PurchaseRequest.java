package ru.example.purchase.model;

import java.math.BigDecimal;

public record PurchaseRequest(Long accountId,
                              String productName,
                              BigDecimal amount) {
}
