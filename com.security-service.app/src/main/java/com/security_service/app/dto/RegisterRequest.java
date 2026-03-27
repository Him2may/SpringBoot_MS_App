package com.security_service.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

//    @NotBlank(message = "First Name is required")
//    private String firstName;

//    @NotBlank(message = "Last Name is required")
//    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Size(max = 18, message = "Password must have at max 18 characters")
    private String password;

    @NotNull(message = "Employee Id is required")
    private long employeeId;
}