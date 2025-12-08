package com.example.user_service.repository;

import com.example.user_service.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserById(UUID userId);
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByPhone(String phone);
    boolean existsByPhone(String phone);
    boolean existsByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.role = 2 ORDER BY u.username ASC")
    List<User> findAllOperators();
}
