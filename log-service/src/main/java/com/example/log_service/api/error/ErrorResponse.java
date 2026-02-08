package com.example.log_service.api.error;

import java.time.LocalDateTime;

public record ErrorResponse(String message, LocalDateTime timestamp) {}
