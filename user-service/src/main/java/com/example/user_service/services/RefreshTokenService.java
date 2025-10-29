package com.example.user_service.services;

import com.example.common_module.errors.CustomJwtException;
import com.example.user_service.domain.entities.Token;
import com.example.user_service.repository.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import io.micrometer.common.lang.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Value("${jwt.refresh-lifetime}")
    private Duration LIFE_TIME;

    private final TokenRepository tokenRepository;

    public String createNewRefresh(UserDetails userDetails) {
        String newRefreshToken = this.generateRefreshToken(userDetails);
        this.save(newRefreshToken, userDetails);
        return newRefreshToken;
    }

    public String getNewRefresh(UserDetails userDetails, String refreshToken) {
        Token refresh = tokenRepository.findByToken(refreshToken)
                .orElseThrow(()->new CustomJwtException("Refresh token not found"));
        if(!isRefreshTokenValid(refreshToken, UUID.fromString(userDetails.getUsername()))) {
            deletePrevRefreshToken(refresh);
            throw new CustomJwtException("Refresh is expired");
        } else {
            String newRefreshToken = this.generateRefreshToken(userDetails);
            this.save(newRefreshToken, userDetails);
            return newRefreshToken;
        }
    }

    private boolean isRefreshTokenExpired(String refreshToken) throws CustomJwtException {
        var expiration = getClaims(refreshToken).getExpiration();
        return expiration.before(new Date());
    }

    private boolean isRefreshTokenValid(String refreshToken, UUID userId) throws CustomJwtException {
        UUID user = this.getUserId(refreshToken);
        return user.equals(userId) && !isRefreshTokenExpired(refreshToken);
    }

    private String generateRefreshToken(UserDetails userDetails) {
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + LIFE_TIME.toMillis());

        return Jwts.builder()
                .setSubject(String.valueOf(userDetails.getUsername()))
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    private void deletePrevRefreshToken(Token refreshToken) {
        tokenRepository.findByToken(refreshToken.getToken())
                .ifPresent(prevToken -> tokenRepository.delete(refreshToken));
    }

    private void save(String refreshToken,  UserDetails userDetails) {
        Token prevToken = tokenRepository
                .findByUserId(UUID.fromString(userDetails.getUsername())).orElse(null);
        Token token = Token.builder()
                .token(refreshToken)
                .userId(UUID.fromString(userDetails.getUsername()))
                .build();
        if(prevToken!=null) {
            deletePrevRefreshToken(prevToken);
        }
        tokenRepository.save(token);
    }

    public UUID getUserId(String refreshToken) {
        Token prevToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomJwtException("Refresh not found"));
        return UUID.fromString(getClaims(refreshToken).getSubject());
    }
    private Claims getClaims(@NonNull String token) throws CustomJwtException {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException | DecodingException | MalformedJwtException | UnsupportedJwtException |
                 SignatureException | IncorrectClaimException e) {
            throw new CustomJwtException(e.getMessage());
        }

    }
}
