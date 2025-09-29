package com.example.auth_service.service;

import com.example.auth_service.domain.dto.AuthResponse;
import com.example.auth_service.domain.dto.LoginRequest;
import com.example.auth_service.domain.dto.Response;
import com.example.auth_service.domain.dto.StaffLoginRequest;

public interface AuthService {
    AuthResponse loginClientUser(LoginRequest request);
    AuthResponse loginStaffUser(StaffLoginRequest request);
    Response logoutUser();
}
