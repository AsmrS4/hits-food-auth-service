package orderservice.service;

import lombok.RequiredArgsConstructor;
import orderservice.dto.FoodDetailsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuExternalService {

    private final WebClient webClient;

    public Mono<FoodDetailsResponse> getMealById(UUID id) {
        return webClient.get()
                .uri("/api/foods/{id}", id)
                .retrieve()
                .bodyToMono(FoodDetailsResponse.class);
    }
}
