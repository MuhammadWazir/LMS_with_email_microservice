package com.example.LMS.book;

import com.example.LMS.service.OpenLibraryService;
import com.example.LMS.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;
    private final OpenLibraryService openLibraryService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookDTO>> createBook(@Valid @RequestBody BookDTO bookDTO) {
        log.info("Received request to create book with ISBN: {}", bookDTO.getIsbn());
        BookDTO createdBook = bookService.createBook(bookDTO);
        ApiResponse<BookDTO> response = ApiResponse.<BookDTO>builder()
                .status("success")
                .message("Book created successfully.")
                .data(createdBook)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookDTO>>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Received request to list all books. Page: {}, Size: {}", page, size);

        Page<BookDTO> booksPage = bookService.getAllBooks(page, size);

        ApiResponse<Page<BookDTO>> response = ApiResponse.<Page<BookDTO>>builder()
                .status("success")
                .message("Books retrieved successfully.")
                .data(booksPage)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/open-library/{isbn}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBookFromOpenLibrary(@PathVariable String isbn) {
        log.info("Received request to fetch book information from Open Library API for ISBN: {}", isbn);
        String authorName = openLibraryService.fetchAuthorName(isbn);

        Map<String, Object> responseData = Map.of(
                "isbn", isbn,
                "authorName", authorName != null ? authorName : "Not found",
                "source", "Open Library API"
        );

        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status("success")
                .message(authorName != null ? "Book information retrieved successfully" : "No book information found")
                .data(responseData)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, authorName != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}