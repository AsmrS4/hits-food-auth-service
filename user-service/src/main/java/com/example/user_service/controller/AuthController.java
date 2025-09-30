package com.example.user_service.controller;

import com.example.user_service.domain.dto.auth.LoginRequest;
import com.example.user_service.domain.dto.auth.StaffLoginRequest;
import com.example.user_service.services.interfaces.AuthService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/user/sign-in")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest request) throws BadRequestException {
        return ResponseEntity.ok(authService.loginClientUser(request));
    }
    @PostMapping("/staff/sign-in")
    public ResponseEntity<?> loginStaffUser(@RequestBody @Valid StaffLoginRequest request) throws BadRequestException {
        return ResponseEntity.ok(authService.loginStaffUser(request));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        return ResponseEntity.ok(authService.logoutUser());
    }
}
