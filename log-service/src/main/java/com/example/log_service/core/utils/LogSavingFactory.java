package com.example.log_service.core.utils;


import com.example.log_service.core.utils.impl.LocalLogSaving;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LogSavingFactory {
    @Value("${settings.strategy}")
    private String STRATEGY;
    public LogSavingStrategy getStrategy() {
        switch (STRATEGY.toLowerCase()) {
            case "local" -> {
                return new LocalLogSaving();
            }
            default -> throw new IllegalArgumentException("Unknown strategy type");
        }
    }
}
