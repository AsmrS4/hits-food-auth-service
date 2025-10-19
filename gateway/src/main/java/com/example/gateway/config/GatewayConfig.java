package com.example.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableHystrix
public class GatewayConfig {
    @Autowired
    private AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/api/users/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user-service"))
                .route("user-service", r -> r.path("/api/about")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user-service"))
                .route("user-service", r -> r.path("/api/auth/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user-service"))
                .route("orders-service", r -> r.path("/api/orders/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://orders-service"))
                .route("foodchain", r -> r.path("/api/foods/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://foodchain"))
                .route("foodchain", r -> r.path("/api/bin/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://foodchain"))
                .route("admin-service", r -> r.path("/api/order/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://admin-service"))
                .build();
    }
}