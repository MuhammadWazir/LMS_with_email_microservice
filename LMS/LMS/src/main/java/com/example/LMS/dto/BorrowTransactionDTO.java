package com.example.LMS.dto;

import com.example.LMS.model.TransactionStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowTransactionDTO {

    private UUID id;

    @NotNull(message = "Book ID cannot be null")
    private UUID bookId;

    @NotBlank(message = "Book title cannot be blank")
    private String bookTitle;

    @NotBlank(message = "Book ISBN cannot be blank")
    @Pattern(regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$",
            message = "Invalid ISBN format")
    private String bookIsbn;

    @NotNull(message = "Borrower ID cannot be null")
    private UUID borrowerId;

    @NotBlank(message = "Borrower name cannot be blank")
    private String borrowerName;

    @NotBlank(message = "Borrower email cannot be blank")
    @Email(message = "Invalid borrower email format")
    private String borrowerEmail;

    @NotNull(message = "Borrow date cannot be null")
    @PastOrPresent(message = "Borrow date cannot be in the future")
    private LocalDate borrowDate;

    @NotNull(message = "Due date cannot be null")
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    private LocalDate returnDate;

    @NotNull(message = "Transaction status cannot be null")
    private TransactionStatus status;

    @DecimalMin(value = "0.0", message = "Fine amount cannot be negative")
    private BigDecimal fineAmount;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isOverdue() {
        return status == TransactionStatus.ACTIVE &&
                dueDate != null &&
                dueDate.isBefore(LocalDate.now());
    }

    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
}
