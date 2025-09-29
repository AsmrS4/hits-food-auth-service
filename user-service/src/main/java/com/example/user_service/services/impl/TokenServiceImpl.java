package com.example.user_service.services.impl;

import com.example.user_service.services.interfaces.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.micrometer.common.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
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
        //TODO: В username хранить userId
        return userId.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    @Override
    public UUID getUserId(String token) {
        return null;
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
