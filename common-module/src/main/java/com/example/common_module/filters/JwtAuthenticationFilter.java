package com.example.common_module.filters;


import com.example.common_module.errors.CustomJwtException;
import com.example.common_module.jwt.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String BEARER_PREFIX;
    private final String HEADER_NAME;
    private final TokenService tokenService;

    @Autowired
    public JwtAuthenticationFilter(TokenService tokenService) {
        this.BEARER_PREFIX = "Bearer ";
        this.HEADER_NAME = "Authorization";
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        var authHeader = request.getHeader(HEADER_NAME);

        if(authHeader!=null && authHeader.startsWith(BEARER_PREFIX)) {
            var jsonWebToken = authHeader.substring(BEARER_PREFIX.length());
            UUID userId = null;
            try {
                userId = tokenService.getUserId(jsonWebToken);
            } catch (CustomJwtException e) {
            }
            if(userId!=null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    if(tokenService.isTokenValid(jsonWebToken, userId)) {
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        UsernamePasswordAuthenticationToken authenticationToken = null;
                        try {
                            authenticationToken = new UsernamePasswordAuthenticationToken(
                                    userId, jsonWebToken, tokenService
                                    .getUserRoles(jsonWebToken).stream()
                                    .map(role-> new SimpleGrantedAuthority(
                                            role.toString()))
                                    .collect(Collectors.toList())
                            );
                        } catch (CustomJwtException e) {
                        }
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        context.setAuthentication(authenticationToken);
                        SecurityContextHolder.setContext(context);
                    }
                } catch (CustomJwtException e) {
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
