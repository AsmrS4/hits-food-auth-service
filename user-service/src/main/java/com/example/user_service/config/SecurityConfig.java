package com.example.user_service.config;

import com.example.user_service.config.filters.ContentTypeFilter;
import com.example.user_service.config.filters.JwtAuthenticationFilter;
import com.example.user_service.handler.AccessDeniedHandlerImpl;
import com.example.user_service.handler.AuthenticationEntryPointImpl;
import com.example.user_service.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ContentTypeFilter contentTypeFilter;
    private final JwtAuthenticationFilter authFilter;
    private final UserService userService;
    private final LogoutHandler logoutHandler;
    private final PasswordEncoder passwordEncoder;
    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }
    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .authorizeHttpRequests(authorizeHttpRequest -> authorizeHttpRequest
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html")
                        .permitAll()
                        .requestMatchers("/api/v1/auth/user/sign-in").permitAll()
                        .requestMatchers("/api/v1/auth/staff/sign-in").permitAll()
                        .requestMatchers("/api/v1/auth/logout").authenticated()
                        .requestMatchers("/api/v1/users/registration/client").permitAll()
                        .requestMatchers("/api/v1/users/registration/admin").permitAll()
                        .requestMatchers("/api/v1/users/registration/operator").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/users/operators").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/users/operators/*").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/users/password/change").authenticated()
                        .requestMatchers("/api/v1/users/me/**").authenticated()
                        .anyRequest().permitAll()
                )
                .sessionManagement(c->c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .addFilterBefore(contentTypeFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.OK.value());
                        })
                );
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
