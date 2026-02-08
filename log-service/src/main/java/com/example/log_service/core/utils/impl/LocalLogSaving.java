package com.example.log_service.core.utils.impl;

import com.example.log_service.api.dto.LogBackendRequest;
import com.example.log_service.api.dto.LogFrontendRequest;
import com.example.log_service.api.mapper.LogMapper;
import com.example.log_service.core.utils.LogSavingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class LocalLogSaving implements LogSavingStrategy {
    @Value("${settings.saving-dir:./files}")
    private String savingDir;
    @Autowired
    private LogMapper logMapper;

    @Override
    public void saveBackendLog(LogBackendRequest rawLog) {
        Path storagePath = getStoragePath();
        if(storagePath != null) {
            File file = getStorageFile(rawLog.getServiceName());
            if(file != null) {
                try(BufferedWriter writer = new BufferedWriter(new FileWriter(file.getName(), true))) {
                    String stringLog = rawLog.toString();
                    writer.write(stringLog);
                    writer.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void saveFrontendLogs(List<LogFrontendRequest> rawLogs) {
        Path storagePath = getStoragePath();
        if(storagePath != null) {
            File file = getStorageFile("frontend");
            if(file != null) {
                try(BufferedWriter writer = new BufferedWriter(new FileWriter(file.getName(), true))) {
                    for(var log: rawLogs) {
                        String stringLog = log.toString();
                        writer.write(stringLog);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private Path getStoragePath(){
        Path storagePath = Paths.get(savingDir);
        try {
            if(!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
            }
            return storagePath;
        } catch (IOException ex) {
            System.out.println("Произошла ошибка");
            ex.printStackTrace();
            return null;
        }
    }

    private File getStorageFile(String serviceName) {
        try {
            File storageFile = new File(savingDir,serviceName + "_logs.txt");
            if (storageFile.createNewFile()) {
                System.out.println("Файл создан: " + storageFile.getName());
            } else {
                System.out.println("Файл уже существует.");
            }
            return storageFile;
        } catch (IOException e) {
            System.out.println("Произошла ошибка");
            e.printStackTrace();
            return null;
        }
    }
}
