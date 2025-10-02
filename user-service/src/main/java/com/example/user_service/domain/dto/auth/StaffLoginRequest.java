package com.example.user_service.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StaffLoginRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "The username length must be between 3 and 100 characters.")
    private String username;
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$", message = "Password must contain at least one letter and one digit and be between 8 and 20 characters")
    private String password;
}
