package com.example.log_service.api.controller;

import com.example.log_service.api.dto.LogBackendRequest;
import com.example.log_service.api.dto.LogFrontendRequest;
import com.example.log_service.core.service.LogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    @PostMapping("/frontend")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendLogs(@RequestBody List<LogFrontendRequest> rawLogs) {
        logService.saveFrontendLogs(rawLogs);
    }

    @PostMapping("/backend")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendLogs(@RequestBody @Valid LogBackendRequest rawLog) {
        logService.saveBackendLog(rawLog);
    }
}
