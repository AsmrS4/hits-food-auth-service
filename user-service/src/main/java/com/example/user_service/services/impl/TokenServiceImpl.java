package com.example.user_service.services.impl;

import com.example.user_service.domain.entities.Token;
import com.example.user_service.domain.entities.User;
import com.example.user_service.repository.TokenRepository;
import com.example.user_service.services.interfaces.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.micrometer.common.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Value("${jwt.lifetime}")
    private Duration LIFE_TIME;
    @Override
    public String getAccessToken(UserDetails userDetails) {
        return generateAccessToken(userDetails);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        var userId = getClaims(token).getSubject();
        var isValid = tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
        return userId.equals(userDetails.getUsername()) && !isTokenExpired(token) && isValid;
    }

    @Override
    public UUID getUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    @Override
    public void revokeAllTokens(User user) {
        List<Token> tokens = tokenRepository.findAllValidToken(user.getId());
        if(!tokens.isEmpty()) {
            tokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(tokens);
        }
    }

    @Override
    public void saveToken(String newAccessToken, User user) {
        var tokenEntity = Token
                .builder()
                .userId(user.getId())
                .token(newAccessToken)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(tokenEntity);
    }

    private boolean isTokenExpired(String token) {
        var expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private String generateAccessToken(UserDetails userDetails) {
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + LIFE_TIME.toMillis());
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts.builder()
                .setSubject(userDetails.getUsername())//TODO:сохранять id пользователя в userDetails
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .claim("role", roles)
                .compact();
    }

    private Claims getClaims(@NonNull String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
