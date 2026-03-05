package com.security_service.app.service;


import com.security_service.app.configuration.JwtUtils;
import com.security_service.app.dto.AuthResponse;
import com.security_service.app.dto.LoginRequest;
import com.security_service.app.dto.RefreshTokenRequest;
import com.security_service.app.dto.RegisterRequest;
import com.security_service.app.entity.RefreshToken;
import com.security_service.app.entity.Role;
import com.security_service.app.entity.User;
import com.security_service.app.entity.UserPrincipal;
import com.security_service.app.exceptions.TokenRefreshException;
import com.security_service.app.repository.RefreshTokenRepository;
import com.security_service.app.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private  final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailService userDetailsService;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @PostConstruct
    public void init(){
        User user = User.builder()
                .email("abcd@gmail.com")
                .password("$2a$12$PeYcBAxfnJeX5TuYmOIgduwEA4.pyDg8vOxo3ieVUGcZ7BwEgZTnS")
                .role(Role.ADMIN)
                .employeeId(1L)
                .build();
        userRepository.save(user);
    }


    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException("Email already registered");
        }

        User user = userDetailsService.registerUser(request);
        String accessToken = jwtUtils.generateAccessToken(user.getEmail()); // Generate tokens
        RefreshToken refreshToken = createRefreshToken(user);
        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    @Transactional
    public AuthResponse refreshTokens(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(refreshToken -> {
                    if (refreshToken.isExpired()) {       // Check if token is expired
                        refreshTokenRepository.delete(refreshToken);
                        throw new TokenRefreshException("Refresh token expired. Please login again.");
                    }
                    User user = refreshToken.getUser();
                    String accessToken = jwtUtils.generateAccessToken(user.getEmail());    // Generate new access token
                    return buildAuthResponse(user,accessToken,refreshToken.getToken());
                })
                .orElseThrow(() -> new TokenRefreshException("Invalid refresh token"));
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByEmail(userPrincipal.getUsername()).get();
        String accessToken = jwtUtils.generateAccessToken(user.getEmail());
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);  // Delete old refresh token if exists and create new one
        RefreshToken refreshToken = createRefreshToken(user);
        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .email(user.getEmail())
                .employeeId(user.getEmployeeId())
                .role(user.getRole().name())
                .build();
    }

}
