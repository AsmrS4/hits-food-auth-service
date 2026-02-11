package com.example.log_service.core.utils.factory;


import com.example.log_service.core.service.interfaces.FileStorageService;
import com.example.log_service.core.utils.strategy.interfaces.LogSavingStrategy;
import com.example.log_service.core.utils.strategy.impl.LocalLogSaving;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LogSavingFactory {
    @Value("${settings.strategy}")
    private String STRATEGY;
    private final FileStorageService fileStorageService;
    public LogSavingStrategy getStrategy() {
        switch (STRATEGY.toLowerCase()) {
            case "local" -> {
                return new LocalLogSaving(fileStorageService);
            }
            default -> {
                log.error("BAD REQUEST");
                throw new IllegalArgumentException("Unknown strategy type");
            }
        }
    }
}
