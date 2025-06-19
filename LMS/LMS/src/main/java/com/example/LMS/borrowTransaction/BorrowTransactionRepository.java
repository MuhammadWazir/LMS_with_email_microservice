package com.example.LMS.borrowTransaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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
    Page<BorrowTransaction> findByBorrowerId(UUID borrowerId, Pageable pageable);

    Page<BorrowTransaction> findByBorrowerIdAndStatus(UUID borrowerId, TransactionStatus status, Pageable pageable);
}
