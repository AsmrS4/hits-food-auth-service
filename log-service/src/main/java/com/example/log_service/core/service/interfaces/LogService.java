package com.example.log_service.core.service.interfaces;

import com.example.log_service.api.dto.LogBackendRequest;
import com.example.log_service.api.dto.LogFrontendRequest;
import com.example.log_service.core.exceptions.FileNotFoundException;
import org.springframework.core.io.Resource;

import java.util.List;

public interface LogService {
    void saveFrontendLogs(List<LogFrontendRequest> rawLogs);
    void saveBackendLog(LogBackendRequest rawLog);
    Resource getFrontendLogFile() throws FileNotFoundException;
    Resource getBackendLogFile(String serviceName) throws FileNotFoundException;
}
