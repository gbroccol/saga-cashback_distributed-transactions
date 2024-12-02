package ru.example.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequest {

    private BigDecimal initBalance;
}
