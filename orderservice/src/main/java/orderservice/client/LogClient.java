package orderservice.client;

import orderservice.dto.LogBackendRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "log-service", url = "${url.log-service-url}")
public interface LogClient {
    @PostMapping("/api/logs/backend")
    void sendLogs(@RequestBody LogBackendRequest rawLog);
}