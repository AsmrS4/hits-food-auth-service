package com.example.log_service.api.controller;

import com.example.log_service.api.dto.LogBackendRequest;
import com.example.log_service.api.dto.LogFrontendRequest;
import com.example.log_service.core.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "LogController")
@Slf4j
public class LogController {
    private final LogService logService;

    @PostMapping("/frontend")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Отправка списка логов",
            description = "Эндпоинт для отправки логов с баг-кейсами для клиентской части",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Успешная обработка",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ошибка клиента",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Ошибка сервера",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    public void sendLogs(@RequestBody List<LogFrontendRequest> rawLogs) {
        logService.saveFrontendLogs(rawLogs);
    }

    @PostMapping("/backend")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Отправка лога",
            description = "Эндпоинт для отправки лога с баг-кейсами для серверной части",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Успешная обработка",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ошибка клиента",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Ошибка сервера",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            }
    )
    public void sendLogs(@RequestBody LogBackendRequest rawLog) {
        logService.saveBackendLog(rawLog);
    }
}
