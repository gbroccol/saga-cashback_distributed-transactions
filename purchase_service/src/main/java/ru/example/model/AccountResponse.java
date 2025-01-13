package ru.example.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountResponse {

    private Long accountId;
    private BigDecimal amount;

}
