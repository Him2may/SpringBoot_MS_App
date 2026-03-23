package com.gateway_service.app.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         AuthenticationWebFilter jwtAuthenticationWebFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers("/api/employee/getAll").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/h2-console/**").permitAll()
                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll().anyExchange().authenticated()
                )
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }

    @Bean
    public AuthenticationWebFilter jwtAuthenticationWebFilter(JwtReactiveAuthenticationManager authManager,
                                                              JwtServerAuthenticationConverter converter) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authManager);
        filter.setServerAuthenticationConverter(converter);
        return filter;
    }

}


/*

Client
  │
  │ POST /api/auth/login
  ▼
Gateway → Auth Service
  │
  │ JWT token returned
  ▼
Client
  │
  │ Authorization: Bearer <token>
  │ GET /api/employee
  ▼
Gateway
  │
  │ AuthenticationWebFilter
  │
  │ JwtServerAuthenticationConverter
  │
  │ JwtReactiveAuthenticationManager
  │
  │ JwtService validates token
  ▼
Employee Service
 */