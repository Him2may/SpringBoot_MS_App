package com.gateway_service.app.security.config;

import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.gateway_service.app.security.service.JwtService;

import java.util.List;
import java.util.Objects;

@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    public JwtReactiveAuthenticationManager(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public @NonNull Mono<Authentication> authenticate(Authentication authentication) {
        String token = Objects.requireNonNull(authentication.getCredentials()).toString();
        if (!jwtService.isTokenValid(token)) {
            return Mono.error(new BadCredentialsException("Invalid JWT token"));
        }

        String username = jwtService.extractUsername(token);
        return Mono.just(new UsernamePasswordAuthenticationToken(username, null, List.of()));
    }
}