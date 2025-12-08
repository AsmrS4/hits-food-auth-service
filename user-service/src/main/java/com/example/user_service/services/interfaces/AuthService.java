package com.example.user_service.services.interfaces;


import com.example.user_service.domain.dto.auth.*;
import org.apache.coyote.BadRequestException;

public interface AuthService {
    AuthResponse loginClientUser(LoginRequest request) throws BadRequestException;
    AuthResponse loginStaffUser(StaffLoginRequest request) throws BadRequestException;

    TokenPair getNewPair(RefreshRequest request) throws BadRequestException;
}
