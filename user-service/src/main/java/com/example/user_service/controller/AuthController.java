package com.example.user_service.controller;

import com.example.user_service.domain.dto.auth.LoginRequest;
import com.example.user_service.domain.dto.auth.StaffLoginRequest;
import com.example.user_service.services.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authorization controller")
public class AuthController {
    private final AuthService authService;
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/user/sign-in")
    @Operation(
            description = "Sign in as client",
            summary = "This is summary for sign in application as client user"
    )
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest request) throws BadRequestException {
        return ResponseEntity.ok(authService.loginClientUser(request));
    }
    @PostMapping("/staff/sign-in")
    @Operation(
            description = "Sign in as staff",
            summary = "This is summary for sign in application as operator or admin user"
    )
    public ResponseEntity<?> loginStaffUser(@RequestBody @Valid StaffLoginRequest request) throws BadRequestException {
        return ResponseEntity.ok(authService.loginStaffUser(request));
    }
}
