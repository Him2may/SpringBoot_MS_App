package com.security_service.app.controller;


import com.security_service.app.dto.*;
import com.security_service.app.dto.password.ChangePassRequest;
import com.security_service.app.dto.password.ForgotPasswordRequest;
import com.security_service.app.dto.password.ResetPasswordRequest;
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

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        AuthResponse authResponse =authService.refreshTokens( refreshTokenRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PutMapping("/change-password")
    public ResponseEntity<AuthResponse> changePassword(@Valid @RequestBody ChangePassRequest request){
        AuthResponse authResponse =authService.changePassword(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> sendForgotPasswordLink(@RequestBody ForgotPasswordRequest request){
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(authService.resetPassword(request));
    }

    @PostMapping("/reset-password-form")
    public ResponseEntity<String> showResetForm(@RequestParam String token){
        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(authService.showResetForm(token));
    }
}
