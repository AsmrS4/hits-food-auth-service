package com.example.user_service.services.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface TokenService {
    public String getAccessToken(UserDetails userDetails);
    boolean isTokenValid(String token, UserDetails userDetails);
    UUID getUserId(String token);
}
