package com.example.log_service.core.service.impl;

import com.example.log_service.api.dto.LogBackendRequest;
import com.example.log_service.api.dto.LogFrontendRequest;
import com.example.log_service.core.service.LogService;
import com.example.log_service.core.utils.LogSavingFactory;
import com.example.log_service.core.utils.LogSavingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final LogSavingFactory factory;

    @Override
    public void saveFrontendLogs(List<LogFrontendRequest> rawLogs) {
        factory.getStrategy().saveFrontendLogs(rawLogs);
    }

    @Override
    public void saveBackendLog(LogBackendRequest rawLog) {
        factory.getStrategy().saveBackendLog(rawLog);
    }
}
