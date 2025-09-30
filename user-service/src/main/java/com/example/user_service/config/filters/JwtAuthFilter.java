package com.example.user_service.config.filters;

import com.example.user_service.services.interfaces.TokenService;
import com.example.user_service.services.interfaces.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final String BEARER_PREFIX;
    private final String HEADER_NAME;
    private final TokenService tokenService;
    private final UserService userService;
    @Autowired
    public JwtAuthFilter(TokenService tokenService, UserService userService) {
        this.BEARER_PREFIX = "Bearer ";
        this.HEADER_NAME = "Authorization";
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader(HEADER_NAME);
        if(authHeader.isEmpty() || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
        }
        var jsonWebToken = authHeader.substring(BEARER_PREFIX.length());
        var userId = tokenService.getUserId(jsonWebToken);
        if(!userId.toString().isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {

        }
    }
}
