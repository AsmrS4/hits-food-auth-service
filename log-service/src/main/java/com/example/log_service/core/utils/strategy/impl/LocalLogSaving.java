package com.example.log_service.core.utils.strategy.impl;

import com.example.log_service.api.dto.LogBackendRequest;
import com.example.log_service.api.dto.LogFrontendRequest;
import com.example.log_service.core.service.interfaces.FileStorageService;
import com.example.log_service.core.utils.strategy.interfaces.LogSavingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class LocalLogSaving implements LogSavingStrategy {
    private final String SAVING_DIR = "./files";
    private final String CLIENT_FILENAME_PREFIX = "frontend";
    private final FileStorageService fileStorageService;

    public LocalLogSaving(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public void saveBackendLog(LogBackendRequest rawLog) {
        Path storagePath = getStoragePath();
        log.info("STORAGE PATH: " + storagePath);
        if(storagePath != null) {
            File file = getStorageFile(rawLog.getServiceName());
            if(file != null) {
                try(BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath(), true))) {
                    String stringLog = rawLog.toString();
                    writeLog(writer, stringLog);
                } catch (IOException ex) {
                    log.error("RECEIVED IO EXCEPTION: " + ex.getMessage() );
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void saveFrontendLogs(List<LogFrontendRequest> rawLogs) {
        Path storagePath = getStoragePath();
        if(storagePath != null) {
            File file = getStorageFile(CLIENT_FILENAME_PREFIX);
            if(file != null) {
                try(BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath(), true))) {
                    for(var log: rawLogs) {
                        String stringLog = log.toString();
                        writeLog(writer, stringLog);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void writeLog(BufferedWriter writer, String log) throws IOException {
        writer.write(log);
        writer.newLine();
        writer.flush();
    }


    private Path getStoragePath(){
        try {
            Path storagePath = Paths.get(SAVING_DIR);
            if(!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
            }
            return storagePath;
        } catch (IOException ex) {
            System.out.println("Произошла ошибка");
            log.error("RECEIVED EX: " + ex.getMessage());
            return null;
        }
    }

    private File getStorageFile(String serviceName) {
        try {
            File storageFile = new File(SAVING_DIR,serviceName + "_logs.txt");
            if (storageFile.createNewFile()) {
                fileStorageService.saveLog(serviceName, storageFile);
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
