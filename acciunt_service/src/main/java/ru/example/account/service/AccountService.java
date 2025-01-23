package ru.example.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.account.exception.AccountDoesNotExistException;
import ru.example.account.exception.InitBalanceLessThanZeroException;
import ru.example.account.exception.NotEnoughMoneyException;
import ru.example.account.model.Account;
import ru.example.account.model.AccountRequest;
import ru.example.account.model.AccountResponse;
import ru.example.account.repo.AccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Value("${cashback.rate}")
    private BigDecimal cashBackRate;

    @Value("${cashback.hundred}")
    private BigDecimal hundred;

    public AccountResponse createAccount(AccountRequest request) {

        if (request.initBalance().compareTo(BigDecimal.ZERO) < 0)
            throw new InitBalanceLessThanZeroException("init balance can't be less than zero");

        Account account = new Account();
        account.setBalance(request.initBalance());
        account.setCashback(BigDecimal.ZERO);
        Account save = accountRepository.save(account);
        return new AccountResponse(save.getId(), save.getBalance(), save.getCashback());
    }

    public AccountResponse getAccount(Long accountId) throws AccountDoesNotExistException {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountDoesNotExistException(String.format("account does not exists (account_id:%d)", accountId)));
        return new AccountResponse(account.getId(), account.getBalance(), account.getCashback());
    }

    @Transactional
    public void withdrawMoney(Long accountId, BigDecimal amount) throws NotEnoughMoneyException, AccountDoesNotExistException {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountDoesNotExistException(String.format("account does not exists (account_id:%d)", accountId)));

        BigDecimal remainder = account.getBalance().subtract(amount);
        if (remainder.compareTo(BigDecimal.ZERO) < 0) {
            throw new NotEnoughMoneyException(String.format("not enough money (account_id:%d)", accountId));
        }
        account.setBalance(remainder);
        accountRepository.save(account);
    }

    @Transactional
    public void addCashBack(Long accountId, BigDecimal amount) throws AccountDoesNotExistException {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountDoesNotExistException(String.format("account does not exists (account_id:%d)", accountId)));
        account.setCashback(account.getCashback().add(
                amount.multiply(cashBackRate.divide(hundred, 4, RoundingMode.HALF_UP))
        ));
        accountRepository.save(account);
    }

    @Transactional
    public void cancelPurchase(Long accountId, BigDecimal amount) throws AccountDoesNotExistException {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountDoesNotExistException(String.format("account does not exists (account_id:%d)", accountId)));
        refundMoney(account, amount);
        cancelCashBack(account, amount);
    }

    private void refundMoney(Account account, BigDecimal amount) {
        BigDecimal remainder = account.getBalance().add(amount);
        account.setBalance(remainder);
    }

    private void cancelCashBack(Account account, BigDecimal amount) {
        BigDecimal cashBack = amount.multiply(cashBackRate.divide(hundred, 4, RoundingMode.HALF_UP));
        account.setCashback(account.getCashback().subtract(cashBack));
    }
}
