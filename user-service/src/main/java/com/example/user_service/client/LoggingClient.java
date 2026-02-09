package com.example.user_service.client;

import com.example.common_module.dto.OperatorDto;
import com.example.user_service.config.ClientConfig;
import com.example.user_service.domain.dto.LogBackendRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "log-service", url = "${url.log-service-url}")
public interface LoggingClient {
    @PostMapping("/api/logs/backend")
    ResponseEntity<?> sendLog(@RequestBody LogBackendRequest request);
}
