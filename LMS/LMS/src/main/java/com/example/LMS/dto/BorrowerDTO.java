package com.example.LMS.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class BorrowerDTO {

    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be in valid format")
    private String email;

    @NotNull(message = "Phone number cannot be null")
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be in valid format")
    private String phoneNumber;

    @NotNull(message = "Address cannot be null")
    @NotBlank(message = "Address cannot be blank")
    @Size(min = 10, max = 255, message = "Address must be between 10 and 255 characters")
    private String address;

}
