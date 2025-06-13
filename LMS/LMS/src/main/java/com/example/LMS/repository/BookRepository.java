package com.example.LMS.repository;

import com.example.LMS.model.Book;
import com.example.LMS.model.BookCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByCategory(BookCategory category);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findByTitleContainingIgnoreCase(@Param("title") String title);

    @Query("SELECT b FROM Book b WHERE LOWER(b.author.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findByAuthorNameContainingIgnoreCase(@Param("authorName") String authorName);
}
