package com.example.log_service.core.service.interfaces;

import com.example.log_service.core.exceptions.FileNotFoundException;
import org.springframework.core.io.Resource;

import java.io.File;

public interface FileStorageService {
    void saveLog(String name, File file);
    Resource findFileByName(String fileName) throws FileNotFoundException;
}
