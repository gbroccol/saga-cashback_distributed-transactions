package ru.example.service;

import ru.example.exception.AccountDoesNotExistException;
import ru.example.exception.NotEnoughMoneyException;

import java.math.BigDecimal;

public interface AccountMoneyOperations {

    void withdrawMoney(Long id, BigDecimal amount) throws AccountDoesNotExistException, NotEnoughMoneyException;
    void addMoney(Long id, BigDecimal amount) throws AccountDoesNotExistException;

}
