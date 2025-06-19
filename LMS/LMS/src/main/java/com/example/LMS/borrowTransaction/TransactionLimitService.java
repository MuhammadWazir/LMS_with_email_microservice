package com.example.LMS.borrowTransaction;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class TransactionLimitService {

    private final BorrowTransactionRepository borrowTransactionRepository;

    @Value("${borrower.transaction.limit}")
    private int transactionLimit;

    public TransactionLimitService(BorrowTransactionRepository borrowTransactionRepository) {
        this.borrowTransactionRepository = borrowTransactionRepository;
    }

    public boolean canBorrowBook(UUID borrowerId) {
        int currentTransactionCount = borrowTransactionRepository.countActiveBorrowingsByBorrowerId(borrowerId);

        log.info("Borrower {} has {} active transactions. Limit is {}",
                borrowerId, currentTransactionCount, transactionLimit);

        return currentTransactionCount < transactionLimit;
    }

    public int getRemainingTransactionLimit(UUID borrowerId) {
        int currentTransactionCount = borrowTransactionRepository.countActiveBorrowingsByBorrowerId(borrowerId);
        return Math.max(0, transactionLimit - currentTransactionCount);
    }

    public int getTransactionLimit() {
        return transactionLimit;
    }
}
