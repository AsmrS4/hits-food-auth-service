package com.example.demo;

import com.example.common_module.config.ClientConfig;
import com.example.common_module.config.ContentTypeConfig;
import com.example.common_module.config.SecurityConfig;
import com.example.common_module.filters.ContentTypeFilter;
import com.example.common_module.filters.JwtAuthenticationFilter;
import com.example.common_module.handler.AccessDeniedHandlerImpl;
import com.example.common_module.handler.AuthenticationEntryPointImpl;
import com.example.common_module.handler.CustomLogoutHandler;
import com.example.common_module.handler.GlobalExceptionHandler;
import com.example.common_module.jwt.TokenService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
		GlobalExceptionHandler.class
})
public class FoodchainApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodchainApplication.class, args);
	}

}
