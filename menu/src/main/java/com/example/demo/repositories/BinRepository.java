package com.example.demo.repositories;

import com.example.demo.entities.BinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BinRepository extends JpaRepository<BinEntity, UUID> {
    Optional<BinEntity> findByClientId(UUID clientId);
}

