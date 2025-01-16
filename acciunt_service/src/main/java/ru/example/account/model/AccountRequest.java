package ru.example.account.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequest {

    private BigDecimal initBalance;
}
