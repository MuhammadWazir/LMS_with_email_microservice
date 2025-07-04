package com.example.LMS.borrowTransaction;

import com.example.LMS.dto.ApiResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            @RequestParam UUID borrowerId,
            @RequestParam int days,
            @RequestParam String card_number,
            @RequestParam String currency) {

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

        BorrowTransactionDTO transaction = borrowTransactionService.borrowBook(bookId, borrowerId, days, card_number, currency);

        ApiResponse<BorrowTransactionDTO> response = ApiResponse.<BorrowTransactionDTO>builder()
                .status("success")
                .message("Book borrowed successfully")
                .data(transaction)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PostMapping("/{transactionId}/return")
    public ResponseEntity<ApiResponse<BorrowTransactionDTO>> returnBook(
            @PathVariable UUID transactionId,
            @RequestParam String card_number,
            @RequestParam String currency) {

        log.info("Received return request for transaction {}", transactionId);

        BorrowTransactionDTO transaction = borrowTransactionService.returnBook(transactionId, card_number, currency);

        ApiResponse<BorrowTransactionDTO> response = ApiResponse.<BorrowTransactionDTO>builder()
                .status("success")
                .message("Book returned successfully")
                .data(transaction)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
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
    @GetMapping("/borrower/{borrowerId}/history")
    public ResponseEntity<ApiResponse<Page<BorrowTransactionDTO>>> getTransactionHistory(
            @PathVariable UUID borrowerId,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching paginated transaction history for borrower {} with status {}, page {}, size {}",
                borrowerId, status, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<BorrowTransactionDTO> transactions = borrowTransactionService.getTransactionHistory(borrowerId, status, pageable);

        ApiResponse<Page<BorrowTransactionDTO>> response = ApiResponse.<Page<BorrowTransactionDTO>>builder()
                .status("success")
                .message("Transaction history retrieved successfully")
                .data(transactions)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
