package com.example.user_service.domain.dto.registration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientRegisterRequest {
    @NotBlank(message = "FullName is required")
    @Size(min = 3, max = 255, message = "The fullName length must be between 3 and 255 characters.")
    private String fullName;
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$", message = "Password must contain at least one letter and one digit and be between 8 and 20 characters")
    private String password;
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(?:\\+?7|8)\\d{10}$", message = "Invalid phone number format")
    private String phone;
}
