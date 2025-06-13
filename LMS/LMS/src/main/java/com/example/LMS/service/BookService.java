package com.example.LMS.service;

import com.example.LMS.dto.BookDTO;
import com.example.LMS.exception.DuplicateResourceException;
import com.example.LMS.model.Author;
import com.example.LMS.model.Book;
import com.example.LMS.repository.AuthorRepository;
import com.example.LMS.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final OpenLibraryService openLibraryService;


    public BookDTO createBook(BookDTO bookDTO) {
        // Check if book with same ISBN already exists
        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new DuplicateResourceException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }

        // Fetch author information from Open Library API
        String fetchedAuthorName = openLibraryService.fetchAuthorName(bookDTO.getIsbn());

        // Use fetched author name if available, otherwise use provided author name
        String authorName = fetchedAuthorName != null ? fetchedAuthorName : bookDTO.getAuthor();

        log.info("Creating book with ISBN: {}. Author from API: {}, Provided author: {}",
                bookDTO.getIsbn(), fetchedAuthorName, bookDTO.getAuthor());

        // Find or create author
        Author author = authorRepository.findByName(authorName)
                .orElseGet(() -> {
                    Author newAuthor = new Author();
                    newAuthor.setName(authorName);
                    return authorRepository.save(newAuthor);
                });

        // Create and save book
        Book book = new Book();
        book.setIsbn(bookDTO.getIsbn());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(author);
        book.setCategory(bookDTO.getCategory());
        book.setLoanPrice(bookDTO.getLoanPrice());
        book.setPublicationYear(bookDTO.getPublicationYear());
        book.setAvailableCopies(bookDTO.getAvailableCopies());
        book.setTotalCopies(bookDTO.getAvailableCopies());

        Book savedBook = bookRepository.save(book);

        return convertToDTO(savedBook);
    }

    private BookDTO convertToDTO(Book book) {
        return new BookDTO(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor().getName(),
                book.getCategory(),
                book.getLoanPrice(),
                book.getPublicationYear(),
                book.getAvailableCopies()
        );
    }

    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(this::convertToDTO).toList();
    }
}
