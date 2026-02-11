package com.example.log_service.core.service.impl;

import com.example.log_service.api.dto.LogBackendRequest;
import com.example.log_service.api.dto.LogFrontendRequest;
import com.example.log_service.core.exceptions.FileNotFoundException;
import com.example.log_service.core.repository.FileRepository;
import com.example.log_service.core.service.interfaces.FileStorageService;
import com.example.log_service.core.service.interfaces.LogService;
import com.example.log_service.core.utils.factory.LogSavingFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final LogSavingFactory factory;
    private final FileStorageService fileStorageService;

    @Override
    public void saveFrontendLogs(List<LogFrontendRequest> rawLogs) {
        factory.getStrategy().saveFrontendLogs(rawLogs);
    }

    @Override
    public void saveBackendLog(LogBackendRequest rawLog) {
        factory.getStrategy().saveBackendLog(rawLog);
    }

    @Override
    public Resource getFrontendLogFile() throws FileNotFoundException {
        return fileStorageService.findFileByName("frontend");
    }

    @Override
    public Resource getBackendLogFile(String serviceName) throws FileNotFoundException {
        return fileStorageService.findFileByName(serviceName);
    }
}
