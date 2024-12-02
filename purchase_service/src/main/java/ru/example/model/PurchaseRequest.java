package ru.example.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseRequest {

    private Long accountId;
    private String productName;
    private BigDecimal amount;
}
