package com.example.LMS.repository;
import com.example.LMS.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {

    Optional<Author> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT a FROM Author a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Author> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT a FROM Author a WHERE a.nationality = :nationality")
    List<Author> findByNationality(@Param("nationality") String nationality);

    @Query("SELECT a FROM Author a WHERE a.birthDate BETWEEN :startDate AND :endDate")
    List<Author> findByBirthDateBetween(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT a, COUNT(b) as bookCount FROM Author a LEFT JOIN a.books b GROUP BY a ORDER BY bookCount DESC")
    List<Object[]> findAuthorsWithBookCount();

    @Query("SELECT a FROM Author a WHERE SIZE(a.books) > :minBooks")
    List<Author> findAuthorsWithMinimumBooks(@Param("minBooks") int minBooks);
}
