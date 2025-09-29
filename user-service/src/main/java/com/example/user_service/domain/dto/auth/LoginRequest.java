package com.example.user_service.domain.dto.auth;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(?:\\+?7|8)\\d{10}$", message = "Invalid phone number format")
    private String phone;
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$", message = "Password must contain at least one letter and one digit and be between 8 and 20 characters")
    private String password;
}
