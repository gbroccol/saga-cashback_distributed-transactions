package ru.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.model.Account;
import ru.example.model.AccountRequest;
import ru.example.model.AccountResponse;
import ru.example.repo.AccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Value("${cashback.rate}")
    private BigDecimal cashBackRate;

    @Value("${cashback.hundred}")
    private BigDecimal hundred;

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        Account account = new Account();
        account.setBalance(request.getInitBalance());
        account.setCashback(BigDecimal.ZERO);
        Account save = accountRepository.save(account);
        return new AccountResponse(save.getId(),save.getBalance(), save.getCashback());
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        Account save = account.orElseThrow(
                () ->  new RuntimeException("Account not found")
        );
        return new AccountResponse(save.getId(),save.getBalance(), save.getCashback());
    }


    @Transactional
    public void addCashBack(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () ->  new RuntimeException("Account not found")
        );
        account.setCashback(account.getCashback().add(
                amount.multiply(cashBackRate.divide(hundred, 4, RoundingMode.HALF_UP))
                ));
        accountRepository.save(account);
    }

    @Transactional
    public void removeCashBack(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () ->  new RuntimeException("Account not found")
        );
        account.setCashback(account.getCashback().subtract(amount.multiply(cashBackRate)));
        accountRepository.save(account);
    }
}
