package ru.example.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.account.exception.AccountDoesNotExistException;
import ru.example.account.model.AccountRequest;
import ru.example.account.model.AccountResponse;
import ru.example.account.service.AccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest request) {
        return new ResponseEntity<>(accountService.createAccount(request), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) throws AccountDoesNotExistException {
        return new ResponseEntity<>(accountService.getAccount(id), HttpStatus.OK);
    }
}
