package com.example.user_service.controller;

import com.example.user_service.domain.dto.ClientRegisterRequest;
import com.example.user_service.domain.dto.ExchangePasswordRequest;
import com.example.user_service.domain.dto.OperatorRegisterRequest;
import com.example.user_service.domain.dto.user.ClientUserDTO;
import com.example.user_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/password/exchange")
    public ResponseEntity<?> changePassword(@RequestBody ExchangePasswordRequest request) {
        return ResponseEntity.ok(null);
    }
    @PostMapping("/registration/client")
    public ResponseEntity<?> registerClientUser(@RequestBody ClientRegisterRequest request){
        return ResponseEntity.ok(userService.registerClientUser(request));
    }

    @PostMapping("/registration/operator")
    public ResponseEntity<?> registerOperatorUser(@RequestBody OperatorRegisterRequest request){
        return ResponseEntity.ok(userService.registerOperatorUser(request));
    }

    @GetMapping("/operators")
    public  ResponseEntity<List<?>> getOperators() {
        return ResponseEntity.ok(userService.getOperators());
    }
}
