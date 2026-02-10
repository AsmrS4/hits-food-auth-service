package com.example.log_service.core.repository;

import com.example.log_service.core.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findByServiceName(String serviceName);
    @Query("SELECT fe FROM FileEntity fe WHERE fe.serviceName LIKE %:serviceName%")
    List<FileEntity> findLikeServiceName(@Param("serviceName") String serviceName);
    boolean existsByServiceName(String serviceName);
}
