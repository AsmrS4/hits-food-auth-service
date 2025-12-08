package com.example.user_service;

import com.example.common_module.config.ContentTypeConfig;
import com.example.common_module.config.SecurityConfig;
import com.example.common_module.dto.OperatorDto;
import com.example.common_module.filters.ContentTypeFilter;
import com.example.common_module.filters.JwtAuthenticationFilter;
import com.example.common_module.handler.AccessDeniedHandlerImpl;
import com.example.common_module.handler.AuthenticationEntryPointImpl;
import com.example.common_module.handler.CustomLogoutHandler;
import com.example.common_module.handler.GlobalExceptionHandler;
import com.example.common_module.jwt.TokenService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
		SecurityConfig.class,
		TokenService.class,
		ContentTypeFilter.class,
		ContentTypeConfig.class,
		JwtAuthenticationFilter.class,
		CustomLogoutHandler.class,
		AuthenticationEntryPointImpl.class,
		AccessDeniedHandlerImpl.class,
		GlobalExceptionHandler.class,
		OperatorDto.class
})
@EnableFeignClients(basePackages = {"com.example.user_service.client"})
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
