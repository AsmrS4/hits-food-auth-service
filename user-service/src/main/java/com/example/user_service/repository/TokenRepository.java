package com.example.user_service.repository;

import com.example.user_service.domain.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("SELECT t FROM Token t INNER JOIN User u ON t.userId = u.id" +
            " WHERE t.userId = :userId AND (t.isExpired = FALSE or t.isRevoked = FALSE)")
    List<Token> findAllValidToken(@Param("userId")UUID userId);
    Optional<Token> findByToken(String token);
}
