package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogRequest {
    @NotBlank(message = "Service name is required")
    private String serviceName;
    @NotBlank(message = "Http method name is required")
    private String method;
    @NotBlank(message = "Endpoint name is required")
    private String endpoint;
    @NotBlank(message = "Http status is required")
    private int status;
    @NotNull(message = "User ID is required")
    private UUID userId;
    @NotNull(message = "TimeStamp is required")
    private LocalDateTime timestamp;
}
