package com.example.universitysocial.service;

import com.example.universitysocial.dto.*;
import com.example.universitysocial.dto.response.ApiResponse;
import com.example.universitysocial.entity.TokenBlacklist;
import com.example.universitysocial.entity.User;
import com.example.universitysocial.repository.TokenBlacklistRepository;
import com.example.universitysocial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final OTPService otpService;
    private final EmailService emailService;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public ApiResponse<String> register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new com.example.universitysocial.exception.ConflictException("Email already registered");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new com.example.universitysocial.exception.ConflictException("Username already taken");
        }

        // Generate OTP
        String otpCode = otpService.generateOTP();
        LocalDateTime otpExpiration = otpService.getOtpExpirationTime();

        // Create user (not enabled until OTP is verified)
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .otpCode(otpCode)
                .otpExpiration(otpExpiration)
                .enabled(false)
                .build();

        userRepository.save(user);

        // Send OTP email
        emailService.sendOtpEmail(user.getEmail(), otpCode);

        log.info("User registered successfully: {}", user.getEmail());
        return ApiResponse.<String>builder()
                .message("Registration successful. Please check your email for OTP verification code.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Transactional
    public ApiResponse<String> verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        // Check if user is already enabled
        if (user.isEnabled()) {
            throw new com.example.universitysocial.exception.ConflictException("Email already verified");
        }

        // Validate OTP
        if (!otpService.validateOtp(request.getOtpCode(), user.getOtpCode(), user.getOtpExpiration())) {
            throw new RuntimeException("Invalid or expired OTP code");
        }

        // Enable user and clear OTP
        user.setEnabled(true);
        user.setOtpCode(null);
        user.setOtpExpiration(null);
        userRepository.save(user);

        log.info("Email verified successfully for user: {}", user.getEmail());
        return ApiResponse.<String>builder()
                .message("Email verified successfully. You can now login.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        // Check if user exists and is enabled
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        if (!user.isEnabled()) {
            throw new RuntimeException("Please verify your email first");
        }

        // Authenticate user
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Generate JWT token
            String jwtToken = jwtService.generateToken(user);

            log.info("User logged in successfully: {}", user.getEmail());
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .tokenType("Bearer")
                    .message("Login successful")
                    .build();
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    @Transactional
    public ApiResponse<String> logout(String token) {
        try {
            // Extract expiration from token
            Date expirationDate = jwtService.getExpirationDate(token);

            // Add token to blacklist
            TokenBlacklist blacklistedToken = TokenBlacklist.builder()
                    .token(token)
                    .expiresAt(LocalDateTime.ofInstant(expirationDate.toInstant(), 
                            java.time.ZoneId.systemDefault()))
                    .build();

            tokenBlacklistRepository.save(blacklistedToken);

            log.info("User logged out successfully");
            return ApiResponse.<String>builder()
                    .message("Logout successful")
                    .status(HttpStatus.OK)
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("Error during logout", e);
            throw new RuntimeException("Logout failed");
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.findByToken(token).isPresent();
    }

    @Transactional
    public void cleanupExpiredTokens() {
        tokenBlacklistRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Expired tokens cleaned up");
    }
}

