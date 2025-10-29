package com.example.user_service.repository;

import com.example.user_service.domain.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    @Query("SELECT t FROM Token t WHERE t.userId=:userId")
    Optional<Token> findByUserId(@Param("userId") UUID userId);
}
