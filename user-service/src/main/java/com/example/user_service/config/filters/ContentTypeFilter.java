package com.example.user_service.config.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ContentTypeFilter implements Filter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        String requestContentType = httpRequest.getContentType();
        log.info(requestContentType);
        if (requestContentType != null && !requestContentType.contains("application/json")) {
            log.warn("Unsupported media type {}", requestContentType);
            httpResponse.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            httpResponse.setContentType("application/json");
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("status", HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            errorDetails.put("error", "Unsupported media type");
            errorDetails.put("message", "Required type is application/json");
            httpResponse.getWriter().write(objectMapper.writeValueAsString(errorDetails));
            return;
        }

        filterChain.doFilter(httpRequest, httpResponse);
    }
}
