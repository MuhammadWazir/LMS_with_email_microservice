package com.example.LMS.controller;

import com.example.LMS.dto.ApiResponse;
import com.example.LMS.dto.EmailRequest;
import com.example.LMS.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/send-email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendEmail(@RequestBody EmailRequest request) {
        emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());

        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status("success")
                .message("Email Sent successfully")
                .data(null)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

