package com.example.log_service.core.service;

import com.example.log_service.api.dto.LogBackendRequest;
import com.example.log_service.api.dto.LogFrontendRequest;

import java.util.List;

public interface LogService {
    void saveFrontendLogs(List<LogFrontendRequest> rawLogs);
    void saveBackendLog(LogBackendRequest rawLog);
}
