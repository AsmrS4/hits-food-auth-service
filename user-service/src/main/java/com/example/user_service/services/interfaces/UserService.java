package com.example.user_service.services.interfaces;

import com.example.user_service.domain.dto.Response;
import com.example.user_service.domain.dto.auth.AuthResponse;
import com.example.user_service.domain.dto.registration.ClientRegisterRequest;
import com.example.user_service.domain.dto.registration.StaffRegisterRequest;
import com.example.user_service.domain.dto.user.*;
import com.example.user_service.domain.entities.User;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

public interface UserService {
    StaffUserDTO registerOperatorUser(StaffRegisterRequest request) throws BadRequestException;
    List<StaffUserDTO> getOperators();
    AuthResponse registerClientUser(ClientRegisterRequest request) throws BadRequestException;
    Response changePassword(ExchangePasswordRequest request) throws BadRequestException;
    UserDetailsService userDetailsService();
    User getCurrentUser();
    UserDTO getUserProfile();
    UserDTO editClientProfile(EditClientDTO dto) throws BadRequestException;
    UserDTO editStaffProfile(EditStaffDTO dto) throws BadRequestException;
    Response deleteOperator(UUID operatorId);
    ClientUserDTO getUserDetails(UUID userId);
}
