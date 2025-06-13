package com.example.LMS.service;

import com.example.LMS.dto.*;
import com.example.LMS.exception.BookNotAvailableException;
import com.example.LMS.exception.ResourceNotFoundException;
import com.example.LMS.exception.TransactionLimitExceededException;
import com.example.LMS.model.Book;
import com.example.LMS.model.BorrowTransaction;
import com.example.LMS.model.Borrower;
import com.example.LMS.dto.EmailRequest;
import com.example.LMS.model.TransactionStatus;
import com.example.LMS.repository.BookRepository;
import com.example.LMS.client.EmailClient;
import com.example.LMS.client.CardClient;
import com.example.LMS.repository.BorrowTransactionRepository;
import com.example.LMS.repository.BorrowerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class BorrowTransactionService {

    private final BorrowTransactionRepository borrowTransactionRepository;
    private final BookRepository bookRepository;
    private final EmailClient emailClient;
    private final CardClient cardClient;
    private final BorrowerRepository borrowerRepository;
    private final TransactionLimitService transactionLimitService;
    @Value("${price.extra}")
    private  BigDecimal extraDaysRentalPrice;
    @Value("${price.insurance}")
    private BigDecimal insuranceRentalPrice;
    public BorrowTransactionService(BorrowTransactionRepository borrowTransactionRepository,
                                    BookRepository bookRepository,
                                    BorrowerRepository borrowerRepository,
                                    TransactionLimitService transactionLimitService,
                                    EmailClient emailClient, CardClient cardClient) {
        this.borrowTransactionRepository = borrowTransactionRepository;
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
        this.transactionLimitService = transactionLimitService;
        this.emailClient = emailClient;
        this.cardClient= cardClient;
    }

    public BorrowTransactionDTO borrowBook(UUID bookId, UUID borrowerId, int days, String card_number, String currency) {
        // Validate borrower exists
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + borrowerId));

        // Check transaction limit
        if (!transactionLimitService.canBorrowBook(borrowerId)) {
            int limit = transactionLimitService.getTransactionLimit();
            throw new TransactionLimitExceededException(
                    String.format("Borrower has reached the maximum transaction limit of %d books", limit)
            );
        }

        // Validate book exists and is available
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book is not available for borrowing");
        }
        BigDecimal book_price = book.getLoanPrice();
        BigDecimal total_price;

        if (days > 7) {
            BigDecimal extraDays = BigDecimal.valueOf(days - 7);
            total_price = extraDays.multiply(extraDaysRentalPrice).add(insuranceRentalPrice).add(book_price);
        } else {
            total_price = book_price.add(insuranceRentalPrice);
        }
        // Create borrow transaction
        BorrowTransaction transaction = new BorrowTransaction();
        transaction.setBook(book);
        transaction.setFineAmount(total_price);
        transaction.setBorrower(borrower);
        transaction.setBorrowDate(LocalDate.now());
        transaction.setDueDate(LocalDate.now().plusDays(days));
        transaction.setStatus(TransactionStatus.ACTIVE);

        // Update book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        CardResponse cardResponse;
        try {
            cardResponse = cardClient.create(new CardRequest(card_number, currency, "D", total_price));
            log.info("Card transaction successful. Transaction ID: {}", cardResponse.getId());
        } catch (Exception e) {
            log.error("Card transaction failed: {}", e.getMessage());

            // Optional: Rollback book availability if already updated
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);

            throw new RuntimeException("Card transaction failed: " + e.getMessage());
        }
        BorrowTransaction savedTransaction = borrowTransactionRepository.save(transaction);
        try {
            EmailRequest emailRequest = new EmailRequest(
                    borrower.getEmail(),
                    "Book Borrowed Successfully",
                    "Dear" + borrower.getName()+"\n Book "+book.getTitle() + " borrowed successfully!"
            );

            emailClient.sendEmail(emailRequest);

            log.info("Email sent successfully to borrower: {}", borrower.getEmail());
        } catch(Exception e) {
            log.error("Could not send email to {}: {}", borrower.getEmail(), e.getMessage());
        }
        log.info("Book borrowed successfully. Transaction ID: {}, Book: {}, Borrower: {}",
                savedTransaction.getId(), book.getTitle(), borrower.getName());

        return convertToDTO(savedTransaction);
    }

    public BorrowTransactionDTO returnBook(UUID transactionId, String cardNumber, String currency) {
        BorrowTransaction transaction = borrowTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));

        if (transaction.getStatus() != TransactionStatus.ACTIVE) {
            throw new IllegalStateException("Book has already been returned.");
        }

        LocalDate returnDate = LocalDate.now();
        transaction.setReturnDate(returnDate);
        transaction.setStatus(TransactionStatus.RETURNED);

        // Refund logic
        boolean isEligibleForRefund = !returnDate.isAfter(transaction.getDueDate());
        if (isEligibleForRefund) {
            try {
                CardResponse refundResponse = cardClient.create(
                        new CardRequest(cardNumber, currency, "C", insuranceRentalPrice)
                );
                log.info("Insurance refund successful. Transaction ID: {}", refundResponse.getId());
            } catch (Exception e) {
                log.error("Insurance refund failed: {}", e.getMessage());
                // Optional: rollback return status or notify
                throw new RuntimeException("Insurance refund failed: " + e.getMessage());
            }
        } else {
            log.info("Book returned after due date. No refund issued.");
        }

        borrowTransactionRepository.save(transaction);

        try {
            EmailRequest emailRequest = new EmailRequest(
                    transaction.getBorrower().getEmail(),
                    "Book Returned",
                    "Dear " + transaction.getBorrower().getName() + ",\nYou returned the book '" + transaction.getBook().getTitle() +
                            "' on " + returnDate + ". " +
                            (isEligibleForRefund ? "Your insurance fee has been refunded." : "No refund due to late return.")
            );
            emailClient.sendEmail(emailRequest);
        } catch (Exception e) {
            log.error("Failed to send return confirmation email to {}: {}", transaction.getBorrower().getEmail(), e.getMessage());
        }

        log.info("Book returned. Transaction ID: {}, Refund applied: {}", transactionId, isEligibleForRefund);
        return convertToDTO(transaction);
    }

    private BorrowTransactionDTO convertToDTO(BorrowTransaction transaction) {
        BorrowTransactionDTO dto = new BorrowTransactionDTO();
        dto.setId(transaction.getId());
        dto.setBookId(transaction.getBook().getId());
        dto.setBookTitle(transaction.getBook().getTitle());
        dto.setBorrowerId(transaction.getBorrower().getId());
        dto.setBorrowerName(transaction.getBorrower().getName());
        dto.setBorrowDate(transaction.getBorrowDate());
        dto.setDueDate(transaction.getDueDate());
        dto.setReturnDate(transaction.getReturnDate());
        dto.setStatus(transaction.getStatus());
        return dto;
    }
}