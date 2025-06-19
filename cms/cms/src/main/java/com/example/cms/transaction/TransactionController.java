package com.example.cms.transaction;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public TransactionResponse create(@RequestBody TransactionRequest request) {
        return service.create(request);
    }
}
