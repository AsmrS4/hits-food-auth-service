package admin_service.client;

import com.example.common_module.config.ClientConfig;
import com.example.common_module.dto.OperatorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", url = "${url.user-service-url}", configuration = ClientConfig.class)
public interface UserClient {
    @GetMapping("/api/users/{operatorId}")
    ResponseEntity<OperatorDto> getOperatorDetails(@PathVariable UUID operatorId);
}
