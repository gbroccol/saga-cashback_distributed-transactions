package ru.example.purchase.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseEvent { // todo record

    private Long accountId;

    private Long purchaseId;

    private BigDecimal amount;
}
