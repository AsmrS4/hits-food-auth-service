package com.example.demo.client;

import com.example.demo.dtos.LogRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "log-service", url = "${url.log-service-url}")
public interface LogClient {
    @PostMapping("/api/logs/backend")
    ResponseEntity<?> sendLog(@RequestBody LogRequest request);
}
