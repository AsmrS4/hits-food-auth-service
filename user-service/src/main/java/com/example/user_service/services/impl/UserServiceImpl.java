package com.example.user_service.services.impl;

import com.example.common_module.dto.OperatorDto;
import com.example.common_module.jwt.TokenService;
import com.example.user_service.domain.dto.Response;
import com.example.user_service.domain.dto.auth.AuthResponse;
import com.example.user_service.domain.dto.registration.ClientRegisterRequest;
import com.example.user_service.domain.dto.registration.StaffRegisterRequest;
import com.example.user_service.domain.dto.user.*;
import com.example.user_service.domain.entities.User;
import com.example.common_module.enums.Role;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.services.RefreshTokenService;
import com.example.user_service.services.interfaces.UserService;
import com.example.user_service.client.OrdersClient;
import com.example.user_service.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper mapper;
    private final OrdersClient client;

    @Override
    public AuthResponse registerClientUser(ClientRegisterRequest request) throws BadRequestException {
        String phoneNumber = validatePhoneNumber(request.getPhone());

        User newUser = mapper.map(request);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setPhone(phoneNumber);
        userRepository.save(newUser);

        String accessToken = tokenService.getAccessToken(newUser);
        String refreshToken = refreshTokenService.createNewRefresh(newUser);
        ClientUserDTO profile = mapper.mapClient(newUser);

        return new AuthResponse(accessToken, refreshToken, profile);
    }

    @Override
    public StaffUserDTO registerOperatorUser(StaffRegisterRequest request) throws BadRequestException {
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException(String.format("Username %s is already taken", request.getUsername()));
        }

        String phoneNumber = validatePhoneNumber(request.getPhone());

        User newUser = mapper.map(request);
        newUser.setPhone(phoneNumber);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(newUser);

        OperatorDto dto = new OperatorDto();
        dto.setId(newUser.getId());
        dto.setFullName(newUser.getFullName());
        dto.setPhone(newUser.getPhone());
        ResponseEntity<?> response = client.saveOperator(dto);

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
        var userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
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
    public UserDTO editClientProfile(EditClientDTO dto) throws BadRequestException {
        User user = getCurrentUser();
        String validatedPhone = dto.getPhone().replaceFirst("^(?:\\+?)7", "8");
        if(userRepository.existsByPhone(validatedPhone)) {
            if(!Objects.equals(user.getPhone(), validatedPhone)) {
                throw new BadRequestException(String.format("Phone %s is already taken", dto.getPhone()));
            }
        }

        dto.setPhone(validatedPhone);
        User updatedUser = mapper.updateUser(user, dto);
        updatedUser = userRepository.save(updatedUser);
        return mapper.mapClient(updatedUser);
    }

    @Override
    public UserDTO editStaffProfile(EditStaffDTO dto) throws BadRequestException {
        User user = getCurrentUser();
        if(userRepository.existsByUsername(dto.getUsername())) {
            if(!Objects.equals(user.getLoginName(), dto.getUsername())) {
                throw new BadRequestException(String.format("Username %s is already taken", dto.getUsername()));
            }
        }
        if(dto.getPhone()!=null) {
            String validatedPhone = dto.getPhone().replaceFirst("^(?:\\+?)7", "8");
            if(userRepository.existsByPhone(validatedPhone)) {
                if(!Objects.equals(user.getPhone(), validatedPhone)) {
                    throw new BadRequestException(String.format("Phone %s is already taken", dto.getPhone()));
                }
            }
            dto.setPhone(validatedPhone);
        }
        User updatedUser = mapper.updateUser(user, dto);
        updatedUser = userRepository.save(updatedUser);
        return mapper.map(updatedUser);
    }

    @Override
    public Response deleteOperator(UUID operatorId) {
        User user = userRepository.findUserById(operatorId)
                .orElseThrow(()-> new UsernameNotFoundException(String.format("Operator with id %s not found", operatorId)));
        userRepository.delete(user);
        ResponseEntity<?> response = client.deleteOperator(operatorId);
        return new Response(HttpStatus.OK, 200, String.format("Operator with id %s was deleted", operatorId));
    }

    @Override
    public ClientUserDTO getUserDetails(UUID userId) {
        User user  = userRepository.findUserById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        if(user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("This information is secured");
        }
        return mapper.mapClient(user);
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
    private String validatePhoneNumber(String phoneNumber) throws BadRequestException {
        String validatedPhone = phoneNumber.replaceFirst("^(?:\\+?)7", "8");
        if(userRepository.existsByPhone(validatedPhone)) {
            throw new BadRequestException(String.format("Phone %s is already taken", phoneNumber));
        }
        return validatedPhone;
    }
}
