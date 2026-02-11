package orderservice.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orderservice.client.LogClient;
import orderservice.dto.LogBackendRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class LogRequest implements HandlerInterceptor {

    private final LogClient logClient;

    private UUID getUserIdFromContext() {
        var userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return UUID.fromString(userId);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        logRequest(request, response);
    }

    private LogBackendRequest createRequestLogBody(HttpServletRequest request, HttpServletResponse response) {
        LogBackendRequest logRequest = new LogBackendRequest();
        logRequest.setEndpoint(request.getRequestURI());
        logRequest.setMethod(request.getMethod());
        logRequest.setServiceName("order-service");
        logRequest.setTimestamp(LocalDateTime.now());
        logRequest.setUserId(getUserIdFromContext());
        logRequest.setStatus(response.getStatus());
        return logRequest;
    }

    public void logRequest(HttpServletRequest request, HttpServletResponse response){
        LogBackendRequest logBackendRequest = createRequestLogBody(request, response);
        try {
            logClient.sendLogs(logBackendRequest);
            log.info("send successfully");
        } catch (Exception e) {
            log.error("Couldn't save request. Log service is unavailable");
            log.error("Ex: " + e.getMessage());
        }
    }

}
