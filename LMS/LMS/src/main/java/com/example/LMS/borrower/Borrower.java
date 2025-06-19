package com.example.LMS.borrower;

import com.example.LMS.borrowTransaction.BorrowTransaction;
import com.example.LMS.borrowTransaction.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.List;
import java.time.*;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Table(name = "borrowers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Borrower {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "membership_date", nullable = false)
    private LocalDate membershipDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private BorrowerStatus status = BorrowerStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowTransaction> borrowTransactions = new ArrayList<>();

    // Helper methods
    public int getActiveBorrowingCount() {
        return (int) borrowTransactions.stream()
                .filter(transaction -> transaction.getStatus() == TransactionStatus.ACTIVE)
                .count();
    }

    public boolean hasOverdueBooks() {
        return borrowTransactions.stream()
                .anyMatch(transaction ->
                        transaction.getStatus() == TransactionStatus.ACTIVE &&
                                transaction.getDueDate().isBefore(LocalDate.now()));
    }
}
