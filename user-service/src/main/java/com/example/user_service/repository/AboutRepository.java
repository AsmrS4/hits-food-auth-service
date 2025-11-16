package com.example.user_service.repository;

import com.example.user_service.domain.entities.About;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AboutRepository extends JpaRepository<About, UUID> {
}
