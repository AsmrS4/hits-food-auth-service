package com.example.common_module.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class ContentTypeFilter implements Filter {

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "application/json",
            "application/x-www-form-urlencoded",
            "multipart/form-data",
            "text/plain"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contentType = httpRequest.getContentType();
        log.info(contentType);

        if (contentType == null || "GET".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        boolean isAllowed = ALLOWED_CONTENT_TYPES.stream()
                .anyMatch(allowed -> contentType.toLowerCase().startsWith(allowed.toLowerCase()));

        if (!isAllowed) {
            log.warn("Unsupported media type {}", contentType);
            httpResponse.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            httpResponse.getWriter().write("Unsupported Media Type");
            return;
        }

        chain.doFilter(request, response);
    }
}