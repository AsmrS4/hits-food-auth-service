package com.example.auth_service.domain.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private Object profile;
}
