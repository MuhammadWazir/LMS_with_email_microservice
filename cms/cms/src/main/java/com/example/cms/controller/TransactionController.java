package com.example.cms.controller;

import com.example.cms.dto.TransactionRequest;
import com.example.cms.dto.TransactionResponse;
import com.example.cms.model.Transaction;
import com.example.cms.service.TransactionService;
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
