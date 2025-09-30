package com.example.user_service.services.impl;


import com.example.user_service.domain.dto.auth.AuthResponse;
import com.example.user_service.domain.dto.auth.LoginRequest;
import com.example.user_service.domain.dto.Response;
import com.example.user_service.domain.dto.auth.StaffLoginRequest;
import com.example.user_service.domain.dto.user.ClientUserDTO;
import com.example.user_service.domain.dto.user.StaffUserDTO;
import com.example.user_service.domain.dto.user.UserDTO;
import com.example.user_service.domain.entities.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.services.interfaces.AuthService;
import com.example.user_service.services.interfaces.TokenService;
import com.example.user_service.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    @Override
    public AuthResponse loginClientUser(LoginRequest request) throws BadRequestException {
        User user = userRepository.findUserByPhone(request.getPhone())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Login failed");
        }
        String accessToken = tokenService.getAccessToken(user);
        ClientUserDTO userProfile = mapper.mapClient(user);
        return new AuthResponse(accessToken, userProfile);
    }

    @Override
    public AuthResponse loginStaffUser(StaffLoginRequest request) throws BadRequestException {
        User user = userRepository.findUserByUsername(request.getUsername())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Login failed");
        }
        String accessToken = tokenService.getAccessToken(user);
        StaffUserDTO userProfile = mapper.map(user);
        return new AuthResponse(accessToken, userProfile);
    }

    @Override
    public Response logoutUser() {
        return null;
    }
}
