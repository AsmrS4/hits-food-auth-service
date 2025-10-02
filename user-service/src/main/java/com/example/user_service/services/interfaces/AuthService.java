package com.example.user_service.services.interfaces;


import com.example.user_service.domain.dto.auth.AuthResponse;
import com.example.user_service.domain.dto.auth.LoginRequest;
import com.example.user_service.domain.dto.auth.StaffLoginRequest;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    AuthResponse loginClientUser(LoginRequest request) throws BadRequestException;
    AuthResponse loginStaffUser(StaffLoginRequest request) throws BadRequestException;
}
