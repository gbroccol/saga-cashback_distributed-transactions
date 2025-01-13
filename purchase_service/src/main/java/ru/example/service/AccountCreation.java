package ru.example.service;

import ru.example.model.AccountRequest;
import ru.example.model.AccountResponse;

public interface AccountCreation {
    AccountResponse create(AccountRequest accountRequest);
}
