package com.example.user_service.repository;

import com.example.user_service.domain.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("SELECT t FROM Token t INNER JOIN User u ON t.user.id = u.id" +
            " WHERE t.user.id =: userId AND (t.is_expired = FALSE or t.is_revoked = FALSE)")
    List<Token> findAllValidToken(@Param("userId")UUID userId);
    Optional<Token> findByToken(String token);
}
