package ru.example.service;

import java.math.BigDecimal;

public interface AccountMoneyOperations {

    void withdrawMoney(Long id, BigDecimal amount);
    void addMoney(Long id, BigDecimal amount);

}
