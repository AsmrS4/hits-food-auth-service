package com.example.user_service.services.impl;

import com.example.user_service.domain.dto.Response;
import com.example.user_service.domain.dto.auth.AuthResponse;
import com.example.user_service.domain.dto.registration.ClientRegisterRequest;
import com.example.user_service.domain.dto.registration.StaffRegisterRequest;
import com.example.user_service.domain.dto.user.ClientUserDTO;
import com.example.user_service.domain.dto.user.ExchangePasswordRequest;
import com.example.user_service.domain.dto.user.StaffUserDTO;
import com.example.user_service.domain.dto.user.UserDTO;
import com.example.user_service.domain.entities.User;
import com.example.user_service.domain.enums.Role;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.services.interfaces.TokenService;
import com.example.user_service.services.interfaces.UserService;
import com.example.user_service.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserMapper mapper;

    @Override
    public AuthResponse registerClientUser(ClientRegisterRequest request) throws BadRequestException {
        String phoneNumber = request.getPhone().replaceFirst("^(?:\\+?)7", "8");
        if(userRepository.existsByPhone(phoneNumber)) {
            throw new BadRequestException(String.format("Phone %s is already taken", request.getPhone()));
        }
        User newUser = mapper.map(request);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setPhone(phoneNumber);
        userRepository.save(newUser);

        String accessToken = tokenService.getAccessToken(newUser);
        ClientUserDTO profile = mapper.mapClient(newUser);
        tokenService.saveToken(accessToken, newUser);

        return new AuthResponse(accessToken, profile);
    }

    @Override
    public StaffUserDTO registerOperatorUser(StaffRegisterRequest request) throws BadRequestException {
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException(String.format("Username %s is already taken", request.getUsername()));
        }
        String phoneNumber = request.getPhone().replaceFirst("^(?:\\+?)7", "8");
        if(userRepository.existsByPhone(phoneNumber)) {
            throw new BadRequestException(String.format("Phone %s is already taken", request.getPhone()));
        }
        User newUser = mapper.map(request);
        newUser.setPhone(phoneNumber);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(newUser);

        return mapper.map(newUser);
    }

    @Override
    public Response changePassword(ExchangePasswordRequest request) throws BadRequestException {
        User currentUser = getCurrentUser();
        if(!passwordEncoder.matches(request.getPassword(), currentUser.getPassword())) {
            throw new BadRequestException("Incorrect password");
        }
        if(request.getPassword().equals(request.getNewPassword())) {
            throw new BadRequestException("Previous password and new password mustn't be equals");
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
        return new Response(HttpStatus.OK, 200, "Password was changed successfully");
    }
    @Override
    public User getCurrentUser(){
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(userId);
    }

    @Override
    public UserDTO getUserProfile() {
        User user = getCurrentUser();
        if(user.getRole().equals(Role.CLIENT)) {
            return mapper.mapClient(user);
        }else {
            return mapper.map(user);
        }
    }

    @Override
    public Response deleteOperator(UUID operatorId) {
        User user = userRepository.findUserById(operatorId)
                .orElseThrow(()-> new UsernameNotFoundException(String.format("Operator with id %s not found", operatorId)));
        userRepository.delete(user);
        return new Response(HttpStatus.OK, 200, String.format("Operator with id %s was deleted", operatorId));
    }

    private User getByUsername(String username) {
        return userRepository.findById(UUID.fromString(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
