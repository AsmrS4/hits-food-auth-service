package com.example.user_service.services.impl;


import com.example.common_module.enums.Role;
import com.example.common_module.jwt.TokenService;
import com.example.user_service.config.UserBugToggles;
import com.example.user_service.domain.dto.auth.*;
import com.example.user_service.domain.dto.user.ClientUserDTO;
import com.example.user_service.domain.dto.user.StaffUserDTO;
import com.example.user_service.domain.entities.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.services.RefreshTokenService;
import com.example.user_service.services.interfaces.AuthService;
import com.example.user_service.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UserBugToggles toggles;
    @Override
    public AuthResponse loginClientUser(LoginRequest request) throws BadRequestException {
        String phoneNumber = request.getPhone().replaceFirst("^(?:\\+?)7", "8");
        User user = userRepository.findUserByPhone(phoneNumber)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        if(!toggles.isEnableStaffAuthViaPhoneNumber()) {
            if(!user.getRole().equals(Role.CLIENT)) {
                throw new AccessDeniedException("Login with client credentials for staff is forbidden");
            }
        }

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Login failed");
        }

        String accessToken = tokenService.getAccessToken(user);
        String refreshToken = refreshTokenService.createNewRefresh(user);
        ClientUserDTO userProfile = mapper.mapClient(user);

        return new AuthResponse(accessToken, refreshToken, userProfile);
    }

    @Override
    public AuthResponse loginStaffUser(StaffLoginRequest request) throws BadRequestException {
        User user = userRepository.findUserByUsername(request.getUsername())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        if(user.getRole().equals(Role.CLIENT)) {
            throw new AccessDeniedException("Login with username for client is forbidden");
        }
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Login failed");
        }

        String accessToken = tokenService.getAccessToken(user);
        String refreshToken = refreshTokenService.createNewRefresh(user);
        StaffUserDTO userProfile = mapper.map(user);

        return new AuthResponse(accessToken, refreshToken, userProfile);
    }

    @Override
    public TokenPair getNewPair(RefreshRequest request) throws BadRequestException {
        if(!toggles.isEnableRefreshSession()) {
            throw new BadRequestException("Session refresh is denied");
        }
        UUID userId = this.refreshTokenService.getUserId(request.getRefreshToken());
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String newRefreshToken = refreshTokenService.getNewRefresh(user, request.getRefreshToken());
        String accessToken = tokenService.getAccessToken(user);
        return new TokenPair(accessToken, newRefreshToken);
    }
}
