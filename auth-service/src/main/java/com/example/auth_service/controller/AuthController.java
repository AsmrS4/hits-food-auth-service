package com.example.auth_service.controller;

import com.example.auth_service.domain.dto.LoginRequest;
import com.example.auth_service.domain.dto.StaffLoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @PostMapping("/user/sign-in")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(null);
    }
    @PostMapping("/staff/sign-in")
    public ResponseEntity<?> loginStaffUser(@RequestBody StaffLoginRequest request) {
        return ResponseEntity.ok(null);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        return ResponseEntity.ok(null);
    }
}
