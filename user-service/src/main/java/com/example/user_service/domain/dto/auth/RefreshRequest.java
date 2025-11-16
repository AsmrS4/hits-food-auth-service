package com.example.user_service.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RefreshRequest {
    @NotBlank(message = "Refresh is required")
    private String refreshToken;
}
