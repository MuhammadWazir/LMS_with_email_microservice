package com.example.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "borrow_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowTransaction {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private Borrower borrower;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.ACTIVE;

    @Column(name = "fine_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal fineAmount = BigDecimal.ZERO;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isOverdue() {
        return status == TransactionStatus.ACTIVE &&
                dueDate.isBefore(LocalDate.now());
    }

    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public BigDecimal calculateFine(BigDecimal dailyFineRate) {
        if (!isOverdue()) {
            return BigDecimal.ZERO;
        }
        return dailyFineRate.multiply(BigDecimal.valueOf(getDaysOverdue()));
    }

    public void returnBook() {
        this.returnDate = LocalDate.now();
        this.status = TransactionStatus.RETURNED;
    }
}
