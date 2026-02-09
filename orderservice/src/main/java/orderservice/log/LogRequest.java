package orderservice.log;

import com.example.log_service.core.enums.HttpMethod;
import lombok.RequiredArgsConstructor;
import orderservice.client.LogClient;
import orderservice.dto.LogBackendRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LogRequest {

    private final LogClient logClient;

    private UUID getUserIdFromContext() {
        var userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return UUID.fromString(userId);
    }

    public void logRequest(HttpMethod method, String endpoint, String status){
        LogBackendRequest logBackendRequest = new LogBackendRequest();
        logBackendRequest.setServiceName("order-service");
        logBackendRequest.setMethod(method);
        logBackendRequest.setUserId(getUserIdFromContext());
        logBackendRequest.setEndpoint(endpoint);
        logBackendRequest.setStatus(status);
        logBackendRequest.setTimestamp(LocalDateTime.now());
        logClient.sendLogs(logBackendRequest);
    }

}
