package com.example.user_service.handler;

import com.example.user_service.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;


@Component
public class CustomLogoutHandler implements LogoutHandler {
    private final String BEARER_PREFIX;
    private final String HEADER_NAME;
    private final TokenRepository tokenRepository;

    @Autowired
    public CustomLogoutHandler(TokenRepository tokenRepository) {
        this.BEARER_PREFIX = "Bearer ";
        this.HEADER_NAME = "Authorization";
        this.tokenRepository = tokenRepository;
    }
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
    {
        var authHeader = request.getHeader(HEADER_NAME);
        if(authHeader!=null && authHeader.startsWith(BEARER_PREFIX)) {
            var jsonWebToken = authHeader.substring(BEARER_PREFIX.length());
            var storedToken = tokenRepository
                    .findByToken(jsonWebToken)
                    .orElse(null);
            if(storedToken!=null) {
                storedToken.setRevoked(true);
                storedToken.setExpired(true);
                tokenRepository.save(storedToken);
            }
        }
    }
}
