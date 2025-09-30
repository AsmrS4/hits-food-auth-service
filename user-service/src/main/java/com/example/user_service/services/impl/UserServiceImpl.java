package com.example.user_service.services.impl;

import com.example.user_service.domain.dto.registration.ClientRegisterRequest;
import com.example.user_service.domain.dto.user.ExchangePasswordRequest;
import com.example.user_service.domain.dto.registration.OperatorRegisterRequest;
import com.example.user_service.domain.dto.Response;
import com.example.user_service.domain.dto.user.StaffUserDTO;
import com.example.user_service.domain.entities.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.services.interfaces.UserService;
import com.example.user_service.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public Object registerClientUser(ClientRegisterRequest request) throws BadRequestException {
        if(userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException(String.format("Phone %s is already taken", request.getPhone()));
        }
        User newUser = mapper.map(request);
        userRepository.save(newUser);
        return null;
    }

    @Override
    public StaffUserDTO registerOperatorUser(OperatorRegisterRequest request) throws BadRequestException {
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException(String.format("Username %s is already taken", request.getUsername()));
        }
        User newUser = mapper.map(request);
        userRepository.save(newUser);

        return mapper.map(newUser);
    }

    @Override
    public Response changePassword(ExchangePasswordRequest request) {
        //TODO: получение данных о пользователе из контекста
        return null;
    }
    @Override
    public User getCurrentUser(){
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(userId);
    }
    private User getByUsername(String username) {
        return userRepository.findById(UUID.fromString(username))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    @Override
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    @Override
    public List<StaffUserDTO> getOperators() {
        List<User> users = userRepository.findAllOperators();
        return mapper.map(users);
    }


}
