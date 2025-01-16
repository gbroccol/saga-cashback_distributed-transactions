package ru.example.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountResponse {

    private Long id;

    private BigDecimal balance;

    private BigDecimal cashBack;
}
