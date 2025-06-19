package com.example.LMS.book;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class BookDTO {

    @NotNull(message = "ISBN cannot be null")
    @NotBlank(message = "ISBN cannot be blank")
    @Pattern(regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$",
            message = "Invalid ISBN format")
    private String isbn;

    @NotNull(message = "Title cannot be null")
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @NotNull(message = "Author cannot be null")
    @NotBlank(message = "Author cannot be blank")
    @Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
    private String author;

    @NotNull(message = "Category cannot be null")
    @Enumerated(EnumType.STRING)
    private BookCategory category;

    @NotNull(message="Loan price cannot be null")
    private BigDecimal loanPrice;

    @NotNull(message = "Publication year cannot be null")
    @Min(value = 1000, message = "Publication year must be at least 1000")
    @Max(value = 2025, message = "Publication year cannot be in the future")
    private Integer publicationYear;

    @NotNull(message = "Available copies cannot be null")
    @Min(value = 0, message = "Available copies cannot be negative")
    private Integer availableCopies;
}