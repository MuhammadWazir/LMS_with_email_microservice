package com.example.LMS.client;

import com.example.LMS.dto.CardRequest;
import com.example.LMS.dto.CardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "card-service", url = "${card.service.url}")
public interface CardClient {
    @PostMapping("/api/transactions")
    CardResponse create(@RequestBody CardRequest cardRequest);
}