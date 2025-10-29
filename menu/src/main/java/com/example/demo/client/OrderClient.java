package com.example.demo.client;

import com.example.common_module.config.ClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "order-service", url = "${url.order-service-url}", configuration = ClientConfig.class)
public interface OrderClient {
    @GetMapping("/order/check-has-ordered/{foodId}")
    ResponseEntity<?> checkHasOrdered(@PathVariable UUID foodId);
}
