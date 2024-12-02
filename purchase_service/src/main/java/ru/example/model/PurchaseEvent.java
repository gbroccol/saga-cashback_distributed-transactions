package ru.example.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseEvent {

    private Long accountId;

    private BigDecimal amount;
}
