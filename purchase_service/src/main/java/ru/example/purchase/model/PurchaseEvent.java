package ru.example.purchase.model;

import java.math.BigDecimal;

public record PurchaseEvent(Long accountId,
                            Long purchaseId,
                            BigDecimal amount) {
}