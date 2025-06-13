package com.example.LMS.service;

import com.example.LMS.dto.BorrowTransactionDTO;
import com.example.LMS.dto.EmailRequest;
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
import com.example.LMS.repository.BorrowTransactionRepository;
import com.example.LMS.repository.BorrowerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class BorrowTransactionService {

    private final BorrowTransactionRepository borrowTransactionRepository;
    private final BookRepository bookRepository;
    private final EmailClient emailClient;
    private final BorrowerRepository borrowerRepository;
    private final TransactionLimitService transactionLimitService;

    public BorrowTransactionService(BorrowTransactionRepository borrowTransactionRepository,
                                    BookRepository bookRepository,
                                    BorrowerRepository borrowerRepository,
                                    TransactionLimitService transactionLimitService,
                                    EmailClient emailClient) {
        this.borrowTransactionRepository = borrowTransactionRepository;
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
        this.transactionLimitService = transactionLimitService;
        this.emailClient = emailClient;
    }

    public BorrowTransactionDTO borrowBook(UUID bookId, UUID borrowerId) {
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

        // Create borrow transaction
        BorrowTransaction transaction = new BorrowTransaction();
        transaction.setBook(book);
        transaction.setBorrower(borrower);
        transaction.setBorrowDate(LocalDate.now());
        transaction.setDueDate(LocalDate.now().plusWeeks(2)); // 2 weeks borrowing period
        transaction.setStatus(TransactionStatus.ACTIVE);

        // Update book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // Save transaction
        BorrowTransaction savedTransaction = borrowTransactionRepository.save(transaction);
        try {
            EmailRequest emailRequest = new EmailRequest(
                    borrower.getEmail(),
                    "Book Borrowed Successfully",
                    "Dear" + borrower.getName()+"\n Book "+book.getTitle() + " borrowed successfully!"
            );

            emailClient.sendEmail(emailRequest); // Using your EmailClient here

            log.info("Email sent successfully to borrower: {}", borrower.getEmail());
        } catch(Exception e) {
            log.error("Could not send email to {}: {}", borrower.getEmail(), e.getMessage());
        }
        log.info("Book borrowed successfully. Transaction ID: {}, Book: {}, Borrower: {}",
                savedTransaction.getId(), book.getTitle(), borrower.getName());

        return convertToDTO(savedTransaction);
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