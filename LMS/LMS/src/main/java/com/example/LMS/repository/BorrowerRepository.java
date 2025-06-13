package com.example.LMS.repository;
import com.example.LMS.model.Borrower;
import com.example.LMS.model.BorrowerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, UUID> {

    Optional<Borrower> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Borrower> findByStatus(BorrowerStatus status);

    @Query("SELECT b FROM Borrower b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Borrower> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT b FROM Borrower b WHERE b.membershipDate BETWEEN :startDate AND :endDate")
    List<Borrower> findByMembershipDateBetween(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM Borrower b WHERE b.phoneNumber = :phoneNumber")
    Optional<Borrower> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT COUNT(bt) FROM BorrowTransaction bt WHERE bt.borrower.id = :borrowerId AND bt.status = 'ACTIVE'")
    int countActiveBorrowingsByBorrowerId(@Param("borrowerId") UUID borrowerId);

    @Query("SELECT b FROM Borrower b WHERE EXISTS (SELECT bt FROM BorrowTransaction bt WHERE bt.borrower = b AND bt.status = 'OVERDUE')")
    List<Borrower> findBorrowersWithOverdueBooks();
}
