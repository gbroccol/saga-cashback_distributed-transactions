package ru.example.account.model;

import java.math.BigDecimal;

public record AccountResponse(Long id,
                              BigDecimal balance,
                              BigDecimal cashBack) {
}