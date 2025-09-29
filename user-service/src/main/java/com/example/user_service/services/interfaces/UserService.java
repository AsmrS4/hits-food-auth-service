package com.example.user_service.services.interfaces;

import com.example.user_service.domain.dto.registration.ClientRegisterRequest;
import com.example.user_service.domain.dto.user.ExchangePasswordRequest;
import com.example.user_service.domain.dto.registration.OperatorRegisterRequest;
import com.example.user_service.domain.dto.Response;
import com.example.user_service.domain.dto.user.StaffUserDTO;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface UserService {
    StaffUserDTO registerOperatorUser(OperatorRegisterRequest request) throws BadRequestException;

    List<StaffUserDTO> getOperators();

    Object registerClientUser(ClientRegisterRequest request) throws BadRequestException;

    Response changePassword(ExchangePasswordRequest request);
}
