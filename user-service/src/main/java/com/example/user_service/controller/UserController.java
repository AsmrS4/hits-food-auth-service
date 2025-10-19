package com.example.user_service.controller;

import com.example.user_service.domain.dto.Response;
import com.example.user_service.domain.dto.about.AboutDTO;
import com.example.user_service.domain.dto.about.EditAbout;
import com.example.user_service.domain.dto.registration.ClientRegisterRequest;
import com.example.user_service.domain.dto.registration.StaffRegisterRequest;
import com.example.user_service.domain.dto.user.*;
import com.example.user_service.services.interfaces.AboutService;
import com.example.user_service.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User controller")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AboutService aboutService;

    @GetMapping("/users/me")
    public ResponseEntity<UserDTO> getProfile() {
        return ResponseEntity.ok(userService.getUserProfile());
    }
    @PutMapping("/users/me")
    public ResponseEntity<UserDTO> updateClientUser(@RequestBody @Valid EditClientDTO dto) throws BadRequestException {
        return ResponseEntity.ok(userService.editClientProfile(dto));
    }
    @PutMapping("/users/me/staff")
    public ResponseEntity<UserDTO> updateStaffUser(@RequestBody @Valid EditStaffDTO dto) throws BadRequestException {
        return ResponseEntity.ok(userService.editStaffProfile(dto));
    }
    @PutMapping("/users/password/change")
    @Operation(
            description = "Change password endpoint",
            summary = "This is summary for change user's password"
    )
    public ResponseEntity<Response> changePassword(@RequestBody @Valid ExchangePasswordRequest request) throws BadRequestException {
        return ResponseEntity.ok(userService.changePassword(request));
    }
    @PostMapping("/users/registration/client")
    @Operation(
            description = "Register user account",
            summary = "This is summary for create account for client"
    )
    public ResponseEntity<?> registerClientUser(@RequestBody @Valid ClientRegisterRequest request) throws BadRequestException {
        return ResponseEntity.ok(userService.registerClientUser(request));
    }
    @PostMapping("/users/registration/operator")
    @Operation(
            description = "Register operator account",
            summary = "This is summary for create account for operator"
    )
    public ResponseEntity<StaffUserDTO> registerOperatorUser(@RequestBody @Valid StaffRegisterRequest request) throws BadRequestException {
        return ResponseEntity.ok(userService.registerOperatorUser(request));
    }
    @DeleteMapping("/users/operators/{operatorId}")
    @Operation(
            description = "Remove operator account",
            summary = "This is summary for remove operator's account from system"
    )
    public ResponseEntity<Response> deleteOperator(@PathVariable UUID operatorId) {
        return ResponseEntity.ok(userService.deleteOperator(operatorId));
    }
    @GetMapping("/users/operators")
    @Operation(
            description = "Get list of operators",
            summary = "This is summary for getting list of operators"
    )
    public  ResponseEntity<List<StaffUserDTO>> getOperators() {
        return ResponseEntity.ok(userService.getOperators());
    }

    @GetMapping("/about")
    public ResponseEntity<AboutDTO> getAboutInfo()  {
        return ResponseEntity.ok(aboutService.getAboutInfo());
    }
    @PutMapping("/about")
    public ResponseEntity<AboutDTO> editAbout(@RequestBody @Valid EditAbout editAbout) {
        return ResponseEntity.ok(aboutService.editAboutInfo(editAbout));
    }
}
