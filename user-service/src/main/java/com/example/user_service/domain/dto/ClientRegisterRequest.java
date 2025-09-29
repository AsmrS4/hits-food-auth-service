package com.example.user_service.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ClientRegisterRequest {
    @NotBlank(message = "FullName is required")
    @Min(value = 3, message = "Min length for fullName field is 3 symbol")
    @Max(value = 255, message = "Max length for fullName field is 255 symbol")
    private String fullName;
    @NotBlank(message = "Password is required")
    @Min(value = 8, message = "Min length for password field is 8 symbol")
    @Max(value = 20, message = "Max length for password field is 20 symbol")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$", message = "Password must contain at least one letter and one digit")
    private String password;
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?7\\d{10}$", message = "Invalid phone number format")
    private String phone;
}
