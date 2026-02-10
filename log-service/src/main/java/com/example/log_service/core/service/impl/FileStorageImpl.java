package com.example.log_service.core.service.impl;

import com.example.log_service.core.exceptions.FileNotFoundException;
import com.example.log_service.core.model.FileEntity;
import com.example.log_service.core.repository.FileRepository;
import com.example.log_service.core.service.interfaces.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileStorageImpl implements FileStorageService {
    private final FileRepository repository;
    @Override
    public void saveLog(String serviceName, File file) {
        FileEntity newFile = new FileEntity();
        newFile.setServiceName(serviceName);
        newFile.setStoragePath(file.getAbsolutePath());

        repository.save(newFile);
    }

    @Override
    public Resource findFileByName(String fileName) throws FileNotFoundException {
        Optional<FileEntity> storedFile = repository.findByServiceName(fileName);
        if(storedFile.isPresent()) {
            File file = new File(storedFile.get().getStoragePath());
            return new FileSystemResource(file);
        } else  {
            throw new FileNotFoundException("Файл с логами не найден");
        }
    }

    @Override
    public boolean existsByServiceName(String serviceName) {
        return repository.existsByServiceName(serviceName);
    }
}
