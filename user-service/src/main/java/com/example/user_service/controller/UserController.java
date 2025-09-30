package com.example.user_service.controller;

import com.example.user_service.domain.dto.registration.ClientRegisterRequest;
import com.example.user_service.domain.dto.user.ExchangePasswordRequest;
import com.example.user_service.domain.dto.registration.OperatorRegisterRequest;
import com.example.user_service.domain.dto.Response;
import com.example.user_service.domain.dto.user.StaffUserDTO;
import com.example.user_service.services.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/password/exchange")
    public ResponseEntity<Response> changePassword(@RequestBody @Valid ExchangePasswordRequest request) {
        return ResponseEntity.ok(userService.changePassword(request));
    }
    @PostMapping("/registration/client")
    public ResponseEntity<?> registerClientUser(@RequestBody @Valid ClientRegisterRequest request) throws BadRequestException {
        return ResponseEntity.ok(userService.registerClientUser(request));
    }

    @PostMapping("/registration/operator")
    public ResponseEntity<StaffUserDTO> registerOperatorUser(@RequestBody @Valid OperatorRegisterRequest request) throws BadRequestException {
        return ResponseEntity.ok(userService.registerOperatorUser(request));
    }
    @PostMapping("/registration/admin")
    public ResponseEntity<StaffUserDTO> registerAdminUser(@RequestBody @Valid OperatorRegisterRequest request) throws BadRequestException {
        return ResponseEntity.ok(userService.registerAdminUser(request));
    }
    @DeleteMapping("/operators/{operatorId}")
    public ResponseEntity<Response> deleteOperator(@PathVariable UUID operatorId) {
        return ResponseEntity.ok(userService.deleteOperator(operatorId));
    }

    @GetMapping("/operators")
    public  ResponseEntity<List<StaffUserDTO>> getOperators() {
        return ResponseEntity.ok(userService.getOperators());
    }
}
