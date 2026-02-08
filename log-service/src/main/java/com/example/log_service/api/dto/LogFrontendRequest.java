package com.example.log_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogFrontendRequest {
    @NotBlank(message = "Log is required")
    private String log;
    @Override
    public String toString() {
        return log;
    }
}
