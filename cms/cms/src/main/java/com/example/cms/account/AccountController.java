package com.example.cms.account;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public AccountResponse create(@RequestBody AccountRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public AccountResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping
    public Page<AccountResponse> getAll(@RequestParam(defaultValue = "0") int page) {
        return service.getAll(page);
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable UUID id, @RequestBody AccountRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
