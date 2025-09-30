package com.example.user_service.domain.dto.registration;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class StaffRegisterRequest extends ClientRegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "The username length must be between 3 and 100 characters.")
    private String username;
}
