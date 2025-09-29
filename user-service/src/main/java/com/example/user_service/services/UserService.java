package com.example.user_service.services;

import com.example.user_service.domain.dto.ClientRegisterRequest;
import com.example.user_service.domain.dto.ExchangePasswordRequest;
import com.example.user_service.domain.dto.OperatorRegisterRequest;
import com.example.user_service.domain.dto.Response;
import com.example.user_service.domain.dto.user.ClientUserDTO;
import com.example.user_service.domain.dto.user.OperatorUserDTO;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface UserService {
    OperatorUserDTO registerOperatorUser(OperatorRegisterRequest request) throws BadRequestException;

    List<OperatorUserDTO> getOperators();

    Object registerClientUser(ClientRegisterRequest request) throws BadRequestException;

    Response changePassword(ExchangePasswordRequest request);
}
