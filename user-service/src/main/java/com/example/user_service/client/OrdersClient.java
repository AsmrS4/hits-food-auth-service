package com.example.user_service.client;

import com.example.common_module.dto.OperatorDto;
import com.example.user_service.config.ClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "order-service", url = "${url.order-service-url}", configuration = ClientConfig.class)
public interface OrdersClient {
    @PostMapping("/order/save-operator")
    ResponseEntity<?> saveOperator(@RequestBody OperatorDto operatorDto);
    @DeleteMapping("/order/delete-operator/{operatorId}")
    ResponseEntity<?> deleteOperator(@PathVariable UUID operatorId);
}