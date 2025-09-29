package com.example.user_service.services;

import com.example.user_service.domain.dto.ClientRegisterRequest;
import com.example.user_service.domain.dto.ExchangePasswordRequest;
import com.example.user_service.domain.dto.OperatorRegisterRequest;
import com.example.user_service.domain.dto.Response;
import com.example.user_service.domain.dto.user.OperatorUserDTO;
import com.example.user_service.domain.entities.User;
import com.example.user_service.domain.enums.Role;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public Object registerClientUser(ClientRegisterRequest request) {
        if(userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException(String.format("Phone %s is already taken", request.getPhone()));
        }
        User newUser = mapper.map(request);
        userRepository.save(newUser);
        return null;
    }

    @Override
    public OperatorUserDTO registerOperatorUser(OperatorRegisterRequest request) {
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException(String.format("Username %s is already taken", request.getUsername()));
        }
        User newUser = mapper.map(request);
        userRepository.save(newUser);

        return mapper.map(newUser);
    }


    @Override
    public Response changePassword(ExchangePasswordRequest request) {
        return null;
    }

    @Override
    public List<OperatorUserDTO> getOperators() {
        List<User> users = userRepository.findAllOperators();
        return mapper.map(users);
    }
}
