package com.example.LMS.controller;

import com.example.LMS.dto.BorrowTransactionDTO;
import com.example.LMS.service.BorrowTransactionService;
import com.example.LMS.service.TransactionLimitService;
import com.example.LMS.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class BorrowTransactionController {

    private final BorrowTransactionService borrowTransactionService;
    private final TransactionLimitService transactionLimitService;

    @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<BorrowTransactionDTO>> borrowBook(
            @RequestParam UUID bookId,
            @RequestParam UUID borrowerId) {

        log.info("Received request to borrow book {} by borrower {}", bookId, borrowerId);

        // Check transaction limit before proceeding
        if (!transactionLimitService.canBorrowBook(borrowerId)) {
            int limit = transactionLimitService.getTransactionLimit();
            ApiResponse<BorrowTransactionDTO> response = ApiResponse.<BorrowTransactionDTO>builder()
                    .status("success")
                    .message(String.format("Transaction limit exceeded. Maximum allowed: %d books", limit))
                    .timestamp(java.time.LocalDateTime.now())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        BorrowTransactionDTO transaction = borrowTransactionService.borrowBook(bookId, borrowerId);

        ApiResponse<BorrowTransactionDTO> response = ApiResponse.<BorrowTransactionDTO>builder()
                .status("success")
                .message("Book borrowed successfully")
                .data(transaction)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/borrower/{borrowerId}/limit")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBorrowerTransactionLimit(
            @PathVariable UUID borrowerId) {

        log.info("Checking transaction limit for borrower {}", borrowerId);

        int limit = transactionLimitService.getTransactionLimit();
        int remaining = transactionLimitService.getRemainingTransactionLimit(borrowerId);

        Map<String, Object> limitInfo = Map.of(
                "borrowerId", borrowerId,
                "maxLimit", limit,
                "remainingLimit", remaining,
                "canBorrow", remaining > 0
        );

        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status("success")
                .message("Transaction limit information retrieved successfully")
                .data(limitInfo)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
