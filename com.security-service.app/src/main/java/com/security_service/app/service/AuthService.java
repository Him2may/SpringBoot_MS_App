package com.security_service.app.service;


import com.security_service.app.configuration.JwtUtils;
import com.security_service.app.dto.*;
import com.security_service.app.dto.password.ChangePassRequest;
import com.security_service.app.dto.password.ForgotPasswordRequest;
import com.security_service.app.dto.password.ResetPasswordRequest;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private  final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Value("${jwt.password-reset-url}")
    private String baseURL;

    @PostConstruct
    public void init(){
        User user = userRepository.findByEmail("abcd@gmail.com").orElse(
                User.builder()
                .email("abcd@gmail.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.ADMIN)
                .employeeId(1L)
                .build());
        userRepository.save(user) ;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException("Email already registered");
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User user = userDetailsService.registerUser(request);
        String accessToken = jwtUtils.generateAccessToken(user.getEmail()); // Generate tokens
        RefreshToken refreshToken = createRefreshToken(user);
        return buildAuthResponse(user, accessToken, refreshToken.getToken(), "User Registered Successfully.");
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        User user = loginUser(loginRequest.getEmail(),loginRequest.getPassword());
        String accessToken = jwtUtils.generateAccessToken(user.getEmail());
        RefreshToken refreshToken = createRefreshToken(user);
        return buildAuthResponse(user, accessToken, refreshToken.getToken(),"Logged in Successfully.");
    }

    @Transactional
    public AuthResponse changePassword(ChangePassRequest restRequest) {
        User user = loginUser(restRequest.getEmail(),restRequest.getCurrentPassword());
        user.setPassword(passwordEncoder.encode(restRequest.getNewPassword()));
        userRepository.save(user);
        String accessToken = jwtUtils.generateAccessToken(user.getEmail());
        RefreshToken refreshToken = createRefreshToken(user);
        return buildAuthResponse(user, accessToken, refreshToken.getToken(),"Password reset Successful.");
    }

    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("purpose","PASSWORD_RESET");
        String resetToken = jwtUtils.generateAccessToken(request.getEmail(),claims);
        sendPassRestLink(request.getEmail(),"user",resetToken);
        return "A password reset link has been sent to your registered Email Id.";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        String message = "";
        String token = request.getResetToken();
        String email = jwtUtils.extractUsername(token);
        if(jwtUtils.validateResetToken(token)) {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found."));
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user = userRepository.save(user);
            refreshTokenRepository.deleteByUser(user);
           message = "Password reset successful!";
        }else{
            message = "Error while resetting password.";
        }

        return """
            <html>
            <body>
                <h3>%s</h3>
                <p>Please Login Again.</p>
            </body>
            </html>
            """.formatted(message);
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
                    return buildAuthResponse(user,accessToken,refreshToken.getToken(),"Access Token Refreshed Successfully.");
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token is NOT valid. Please login again."));
    }

    private RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public User loginUser(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getUser();
        }catch (BadCredentialsException e) {
            throw  new BadCredentialsException("Invalid email or password");
        }
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken,String message) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .email(user.getEmail())
                .employeeId(user.getEmployeeId())
                .role(user.getRole().name())
                .message(message)
                .build();
    }

    private String sendPassRestLink(String email,String name, String resetToken) {
        String resetLink = baseURL + resetToken;
        String emailBody = """
        Dear %s

        Please click on the below link to reset your password:

        %s
        This link expires in 15 minutes.
        If you didn’t request it, Don't share it with others.
        """.formatted(name, resetLink);
        System.out.println(emailBody);
        return emailBody;
    }

    public String showResetForm(String token) {

        String html = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Reset Password</title>
        </head>
        <body>
            <h2>Reset Your Password</h2>
            <form method="POST" action="http://localhost:9192/api/auth/reset-password">
                <input type="hidden" name="resetToken" value="%s"/>

                <label>New Password:</label><br/>
                <input type="password" name="newPassword" required/><br/><br/>

                <button type="submit">Reset Password</button>
            </form>
        </body>
        </html>
        """.formatted(token);
        return html;
    }
}
