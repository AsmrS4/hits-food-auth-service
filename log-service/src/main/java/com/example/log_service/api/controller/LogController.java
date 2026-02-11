package com.example.log_service.api.controller;

import com.example.log_service.api.dto.LogBackendRequest;
import com.example.log_service.api.dto.LogFrontendRequest;
import com.example.log_service.core.exceptions.FileNotFoundException;
import com.example.log_service.core.service.interfaces.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @GetMapping("/frontend")
    @Operation(
            summary = "Получение файла с логами",
            description = "Эндпоинт для получения файла с логами клиентской части"
    )
    public ResponseEntity<Resource> getFrontendLogFile() throws FileNotFoundException, IOException {
        Resource resource = logService.getFrontendLogFile();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=frontend_logs.txt")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .contentLength(resource.getFile().length())
                .body(resource);
    }

    @GetMapping("/backend")
    @Operation(
            summary = "Получение файла с логами",
            description = "Эндпоинт для получения файла с логами серверной части"
    )
    public ResponseEntity<Resource> getBackendLogFile(@RequestParam String serviceName) throws IOException, FileNotFoundException {
        Resource resource = logService.getBackendLogFile(serviceName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + serviceName + "_logs.txt\"")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .contentLength(resource.getFile().length())
                .body(resource);
    }
}
