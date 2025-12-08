package com.example.user_service.config;

import feign.RequestInterceptor;
import feign.httpclient.ApacheHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class ClientConfig {
    private String getAuthToken() {
        return SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
    }
    @Bean
    public RequestInterceptor authorizationInterceptor() {

        return template -> {
            String authToken = "Bearer " + getAuthToken();
            template.header("Authorization", authToken);
        };
    }
    @Bean
    public ApacheHttpClient apacheHttpClient() {
        return new ApacheHttpClient();
    }
}
