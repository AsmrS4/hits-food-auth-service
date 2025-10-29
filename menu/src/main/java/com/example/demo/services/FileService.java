package com.example.demo.services;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

@Service
public class FileService {

    @Value("${app.file-storage.upload-dir}")
    private String uploadDir;

    @Value("${app.file-storage.base-url}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Файл пустой");
            }

            // Генерируем уникальное имя файла
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            String fileName = UUID.randomUUID().toString() + fileExtension;

            // Сохраняем файл
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Возвращаем полный URL
            return baseUrl + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл: " + e.getMessage(), e);
        }
    }

    public byte[] loadFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir + fileName);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить файл: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl != null && !fileUrl.isEmpty()) {
                // Извлекаем имя файла из URL
                String fileName = extractFileNameFromUrl(fileUrl);
                Path filePath = Paths.get(uploadDir + fileName);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось удалить файл: " + e.getMessage(), e);
        }
    }

    private String extractFileNameFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    private String getFileExtension(String fileName) {
        return fileName != null && fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf("."))
                : "";
    }
}
