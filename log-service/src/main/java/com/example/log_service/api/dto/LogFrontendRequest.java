package com.example.log_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogFrontendRequest {
    @NotBlank(message = "Log is required")
    private String log;
}
