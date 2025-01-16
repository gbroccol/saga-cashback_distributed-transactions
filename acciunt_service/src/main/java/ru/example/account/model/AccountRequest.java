package ru.example.account.model;

import java.math.BigDecimal;

public record AccountRequest(BigDecimal initBalance) {
}
