package com.example.user_service.config.filters;


import com.example.user_service.services.interfaces.TokenService;
import com.example.user_service.services.interfaces.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String BEARER_PREFIX;
    private final String HEADER_NAME;
    private final TokenService tokenService;
    private final UserService userService;
    @Autowired
    public JwtAuthenticationFilter(TokenService tokenService, UserService userService) {
        this.BEARER_PREFIX = "Bearer ";
        this.HEADER_NAME = "Authorization";
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader(HEADER_NAME);

        if(authHeader!=null && authHeader.startsWith(BEARER_PREFIX)) {
            var jsonWebToken = authHeader.substring(BEARER_PREFIX.length());
            var userId = tokenService.getUserId(jsonWebToken);

            if(userId!=null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService
                        .userDetailsService()
                        .loadUserByUsername(String.valueOf(userId));

                if(tokenService.isTokenValid(jsonWebToken, userDetails)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authenticationToken);
                    SecurityContextHolder.setContext(context);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
