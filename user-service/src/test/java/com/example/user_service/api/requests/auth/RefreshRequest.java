package com.example.user_service.api.requests.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshRequest {
    private String refreshToken;
}
