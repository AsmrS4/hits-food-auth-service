package orderservice.client;

import com.example.common_module.config.ClientConfig;
import orderservice.dto.FoodDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "menu-service", url = "${url.menu-service-url}", configuration = ClientConfig.class)
public interface DishClient {
    @GetMapping("/api/foods/{id}")
    ResponseEntity<FoodDetailsResponse> getFoodDetails(@PathVariable UUID id);
}