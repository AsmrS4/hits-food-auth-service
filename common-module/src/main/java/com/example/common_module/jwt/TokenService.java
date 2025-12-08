package com.example.common_module.jwt;

import com.example.common_module.errors.CustomJwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
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
public class TokenService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Value("${jwt.lifetime}")
    private Duration LIFE_TIME;
    public String getAccessToken(UserDetails userDetails) {
        return generateAccessToken(userDetails);
    }

    public boolean isTokenValid(String token, UUID userId) throws CustomJwtException {
        UUID user = UUID.fromString(getClaims(token).getSubject());
        return user.equals(userId) && !isTokenExpired(token);
    }
    public List<?> getUserRoles(String token) throws CustomJwtException {
        return getClaims(token).get("role", List.class);
    }

    public UUID getUserId(String token) throws CustomJwtException {
        return UUID.fromString(getClaims(token).getSubject());
    }

    private boolean isTokenExpired(String token) throws CustomJwtException {
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

    private Claims getClaims(@NonNull String token) throws CustomJwtException {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException | DecodingException | MalformedJwtException | UnsupportedJwtException | SignatureException | IncorrectClaimException e) {
            throw new CustomJwtException(e.getMessage());
        }

    }
}
