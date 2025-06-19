package com.example.LMS.borrower;

import com.example.LMS.exception.DuplicateResourceException;
import com.example.LMS.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;

    public BorrowerService(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    public BorrowerDTO createBorrower(@Valid BorrowerDTO borrowerDTO) {
        if (borrowerRepository.existsByEmail(borrowerDTO.getEmail())) {
            throw new DuplicateResourceException("Borrower with email '" + borrowerDTO.getEmail() + "' already exists");
        }

        Borrower borrower = Borrower.builder()
                .name(borrowerDTO.getName())
                .email(borrowerDTO.getEmail())
                .phoneNumber(borrowerDTO.getPhoneNumber())
                .address(borrowerDTO.getAddress())
                .membershipDate(LocalDate.now())
                .status(BorrowerStatus.ACTIVE)
                .build();

        Borrower savedBorrower = borrowerRepository.save(borrower);
        log.info("Created new borrower: {}", savedBorrower.getName());

        return convertToDTO(savedBorrower);
    }

    public Page<BorrowerDTO> getAllBorrowers(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Borrower> borrowersPage = borrowerRepository.findAll(pageable);

        return borrowersPage.map(borrower -> new BorrowerDTO(
                borrower.getName(),
                borrower.getEmail(),
                borrower.getPhoneNumber(),
                borrower.getAddress()
        ));
    }
    public BorrowerDTO getBorrowerById(UUID id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));
        return convertToDTO(borrower);
    }

    public BorrowerDTO updateBorrower(UUID id, BorrowerDTO borrowerDTO) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));

        // Check if email is being changed and if new email already exists
        if (!borrower.getEmail().equals(borrowerDTO.getEmail()) &&
                borrowerRepository.existsByEmail(borrowerDTO.getEmail())) {
            throw new DuplicateResourceException("Borrower with email '" + borrowerDTO.getEmail() + "' already exists");
        }

        borrower.setName(borrowerDTO.getName());
        borrower.setEmail(borrowerDTO.getEmail());
        borrower.setPhoneNumber(borrowerDTO.getPhoneNumber());
        borrower.setAddress(borrowerDTO.getAddress());

        Borrower updatedBorrower = borrowerRepository.save(borrower);
        log.info("Updated borrower: {}", updatedBorrower.getName());

        return convertToDTO(updatedBorrower);
    }

    public void deleteBorrower(UUID id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));

        borrowerRepository.delete(borrower);
        log.info("Deleted borrower: {}", borrower.getName());
    }

    public BorrowerDTO suspendBorrower(UUID id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));

        borrower.setStatus(BorrowerStatus.SUSPENDED);
        Borrower updatedBorrower = borrowerRepository.save(borrower);

        log.info("Suspended borrower: {}", borrower.getName());
        return convertToDTO(updatedBorrower);
    }

    public BorrowerDTO activateBorrower(UUID id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));

        borrower.setStatus(BorrowerStatus.ACTIVE);
        Borrower updatedBorrower = borrowerRepository.save(borrower);

        log.info("Activated borrower: {}", borrower.getName());
        return convertToDTO(updatedBorrower);
    }

    private BorrowerDTO convertToDTO(Borrower borrower) {
        BorrowerDTO dto = new BorrowerDTO();
        dto.setName(borrower.getName());
        dto.setEmail(borrower.getEmail());
        dto.setPhoneNumber(borrower.getPhoneNumber());
        dto.setAddress(borrower.getAddress());
        return dto;
    }
}