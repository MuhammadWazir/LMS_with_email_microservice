package com.example.cms.controller;

import com.example.cms.model.Account;
import com.example.cms.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public Account create(@RequestBody Account account) {
        return service.create(account);
    }

    @GetMapping("/{id}")
    public Account get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping
    public List<Account> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public Account update(@PathVariable UUID id, @RequestBody Account account) {
        return service.update(id, account);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
