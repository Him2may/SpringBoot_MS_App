package com.security_service.app.controller;


import com.security_service.app.dto.AuthResponse;
import com.security_service.app.dto.LoginRequest;
import com.security_service.app.dto.RefreshTokenRequest;
import com.security_service.app.dto.RegisterRequest;
import com.security_service.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest){
        AuthResponse authResponse =authService.register(registerRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        System.out.println("Login Request: " + loginRequest);
        AuthResponse authResponse =authService.login( loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        AuthResponse authResponse =authService.refreshTokens( refreshTokenRequest);
        return ResponseEntity.ok(authResponse);
    }

}
