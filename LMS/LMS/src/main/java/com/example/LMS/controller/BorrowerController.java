package com.example.LMS.controller;

import com.example.LMS.dto.BorrowerDTO;
import com.example.LMS.service.BorrowerService;
import com.example.LMS.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/borrowers")
@RequiredArgsConstructor
@Slf4j
public class BorrowerController {

    private final BorrowerService borrowerService;

    @PostMapping
    public ResponseEntity<ApiResponse<BorrowerDTO>> createBorrower(@Valid @RequestBody BorrowerDTO borrowerDTO) {
        log.info("Received request to create borrower with email: {}", borrowerDTO.getEmail());
        BorrowerDTO createdBorrower = borrowerService.createBorrower(borrowerDTO);
        ApiResponse<BorrowerDTO> response = ApiResponse.<BorrowerDTO>builder()
                .status("success")
                .message("Borrower created successfully.")
                .data(createdBorrower)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BorrowerDTO>>> getAllBorrowers() {
        log.info("Received request to list all borrowers.");
        List<BorrowerDTO> borrowers = borrowerService.getAllBorrowers();
        ApiResponse<List<BorrowerDTO>> response = ApiResponse.<List<BorrowerDTO>>builder()
                .status("success")
                .message("Borrowers retrieved successfully.")
                .data(borrowers)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}