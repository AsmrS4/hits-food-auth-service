package com.example.log_service.core.utils;


import com.example.log_service.core.utils.impl.LocalLogSaving;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogSavingFactory {
    @Value("${settings.strategy}")
    private String STRATEGY;
    public LogSavingStrategy getStrategy() {
        switch (STRATEGY.toLowerCase()) {
            case "local" -> {
                return new LocalLogSaving();
            }
            default -> {
                log.error("BAD REQUEST");
                throw new IllegalArgumentException("Unknown strategy type");
            }
        }
    }
}
