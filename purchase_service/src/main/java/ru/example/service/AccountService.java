package ru.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.exception.AccountDoesNotExistException;
import ru.example.exception.NotEnoughMoneyException;
import ru.example.model.*;
import ru.example.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

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
    @Transactional
    public void withdrawMoney(Long accountId, BigDecimal amountToSubtract) {

        Optional<Account> optionalAccount = accountRepository.findById(accountId);

        if (optionalAccount.isEmpty()) {
            throw new AccountDoesNotExistException(String.format("error - account does not exists (account_id:%d)", accountId));
        }

        Account account = optionalAccount.get();

        BigDecimal remainder = account.getAmount().subtract(amountToSubtract);
        if (remainder.compareTo(BigDecimal.ZERO) < 0) {
            throw new NotEnoughMoneyException(String.format("error - not enough money (account_id:%d)", accountId));
        }
        account.setAmount(remainder);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void addMoney(Long accountId, BigDecimal amountToAdd) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountDoesNotExistException(String.format("error - account does not exists (account_id:%d)", accountId)));

        BigDecimal remainder = account.getAmount().add(amountToAdd);
        account.setAmount(remainder);
        accountRepository.save(account);
    }

}
