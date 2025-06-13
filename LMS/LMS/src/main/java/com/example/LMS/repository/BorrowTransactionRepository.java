package com.example.LMS.repository;

import com.example.LMS.model.BorrowTransaction;
import com.example.LMS.model.Borrower;
import com.example.LMS.model.TransactionStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BorrowTransactionRepository extends JpaRepository<BorrowTransaction, UUID> {

    @Query("SELECT COUNT(bt) FROM BorrowTransaction bt WHERE bt.borrower.id = :borrowerId AND bt.status = 'ACTIVE'")
    int countActiveBorrowingsByBorrowerId(@Param("borrowerId") UUID borrowerId);

    List<BorrowTransaction> findByBorrowerIdAndStatus(UUID borrowerId, TransactionStatus status);

    List<BorrowTransaction> findByBookIdAndStatus(UUID bookId, TransactionStatus status);

    @Query("SELECT bt FROM BorrowTransaction bt WHERE bt.dueDate < CURRENT_DATE AND bt.status = 'ACTIVE'")
    List<BorrowTransaction> findOverdueTransactions();
}
