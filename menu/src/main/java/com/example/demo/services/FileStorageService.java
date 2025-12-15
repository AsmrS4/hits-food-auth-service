package com.example.demo.services;

import com.example.demo.config.FileStorageConfig;
import com.example.demo.config.FeatureToggles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final FileStorageConfig fileStorageConfig;
    private final FeatureToggles features;
    @Value("${server.protocol}")
    private String PROTOCOL;
    @Value("${server.host}")
    private String SERVER_HOST;
    @Value("${server.port}")
    private String SERVER_PORT;

    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file");
            }

            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            String fileName = UUID.randomUUID() + (fileExtension != null ? fileExtension : "");

            Path storagePath = fileStorageConfig.getStoragePath();
            Files.createDirectories(storagePath);

            Path targetLocation = storagePath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            if (features.isBugCorruptPhotosPaths()) {
                return "/uploads/" + UUID.randomUUID() + ".bug";
            }

            return String.format("%s://%s:%s", PROTOCOL, SERVER_HOST, SERVER_PORT) + "/uploads/" + fileName;

        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public List<String> storeFiles(List<MultipartFile> files) {
        List<String> storedFilePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String filePath = storeFile(file);
                storedFilePaths.add(filePath);
            }
        }
        return storedFilePaths;
    }

    public void deleteFile(String filePath) {
        try {
            if (filePath != null && filePath.startsWith("/uploads/")) {
                String fileName = filePath.substring("/uploads/".length());
                Path fileToDelete = fileStorageConfig.getStoragePath().resolve(fileName);
                Files.deleteIfExists(fileToDelete);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
        }
    }

    public void deleteFiles(List<String> filePaths) {
        if (filePaths != null) {
            filePaths.forEach(this::deleteFile);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}