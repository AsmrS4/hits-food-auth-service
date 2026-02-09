package orderservice.client;

import com.example.common_module.config.ClientConfig;
import orderservice.dto.LogBackendRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "log-service", url = "${url.log-service-url}", configuration = ClientConfig.class)
public interface LogClient {
    @PostMapping("/backend")
    public void sendLogs(@RequestBody LogBackendRequest rawLog);
}