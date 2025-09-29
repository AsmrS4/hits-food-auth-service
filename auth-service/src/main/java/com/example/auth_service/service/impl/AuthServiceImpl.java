package com.example.auth_service.service.impl;

import com.example.auth_service.domain.dto.AuthResponse;
import com.example.auth_service.domain.dto.LoginRequest;
import com.example.auth_service.domain.dto.Response;
import com.example.auth_service.domain.dto.StaffLoginRequest;
import com.example.auth_service.service.AuthService;

public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponse loginClientUser(LoginRequest request) {
        return null;
    }

    @Override
    public AuthResponse loginStaffUser(StaffLoginRequest request) {
        return null;
    }

    @Override
    public Response logoutUser() {
        return null;
    }
}
