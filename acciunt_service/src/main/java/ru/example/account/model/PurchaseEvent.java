package ru.example.account.model;

import java.math.BigDecimal;

public record PurchaseEvent(Long accountId,
                            Long purchaseId,
                            BigDecimal amount) {
}