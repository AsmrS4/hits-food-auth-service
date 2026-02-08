package com.example.log_service.core.utils;

import com.example.log_service.api.dto.LogBackendRequest;
import com.example.log_service.api.dto.LogFrontendRequest;

import java.util.List;

public interface LogSavingStrategy {
    void saveBackendLog(LogBackendRequest rawLog);
    void saveFrontendLogs(List<LogFrontendRequest> rawLogs);
}
