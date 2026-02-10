package com.example.user_service.config;

import com.example.user_service.client.LoggingClient;
import com.example.user_service.domain.dto.LogBackendRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private final LoggingClient loggingClient;
    private final String SERVICE_NAME = "user-service";

    @Lazy
    public RequestLoggingInterceptor(LoggingClient loggingClient) {
        this.loggingClient = loggingClient;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        LogBackendRequest logRequest = createRequestLogBody(request, response);
        log.info("SEND log: " + logRequest.toString());
        try {
            loggingClient.sendLog(logRequest);
            log.info("send successfully");
        } catch (Exception e) {
            log.error("Couldn't save request. Log service is unavailable");
            log.error("Ex: " + e.getMessage());
        }
    }

    private LogBackendRequest createRequestLogBody(HttpServletRequest request, HttpServletResponse response) {
        LogBackendRequest logRequest = new LogBackendRequest();
        logRequest.setEndpoint(request.getRequestURI());
        logRequest.setMethod(request.getMethod());
        logRequest.setServiceName(SERVICE_NAME);
        logRequest.setTimestamp(LocalDateTime.now());
        logRequest.setUserId(getUserIdFromContext());
        logRequest.setStatus(response.getStatus());
        return logRequest;
    }

    private UUID getUserIdFromContext() {
        var userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
