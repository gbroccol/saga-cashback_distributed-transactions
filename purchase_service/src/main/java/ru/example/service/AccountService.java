package ru.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.example.model.*;
import ru.example.repository.AccountRepository;

import java.math.BigDecimal;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountService implements AccountCreation, AccountMoneyOperations {

    private final AccountRepository accountRepository;

    @Override
    public AccountResponse create(AccountRequest accountRequest) {

        Account account = new Account();
        account.setAmount(accountRequest.getAmount());
        account = accountRepository.save(account);

        AccountResponse resp = new AccountResponse();
        resp.setAccountId(account.getId());
        resp.setAmount(account.getAmount());

        return resp;
    }

    @Override
    public void withdrawMoney(Long id, BigDecimal amount) {
        // todo
    }

    @Override
    public void addMoney(Long id, BigDecimal amount) {
        // todo
    }

}
