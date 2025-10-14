package com.example.common_module.handler;

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

    @Autowired
    public CustomLogoutHandler() {
        this.BEARER_PREFIX = "Bearer ";
        this.HEADER_NAME = "Authorization";
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
        }
    }
}
