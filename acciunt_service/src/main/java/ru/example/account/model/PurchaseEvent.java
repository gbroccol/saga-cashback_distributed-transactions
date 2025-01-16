package ru.example.account.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseEvent {

    private Long accountId;

    private Long purchaseId;

    private BigDecimal amount;
}
