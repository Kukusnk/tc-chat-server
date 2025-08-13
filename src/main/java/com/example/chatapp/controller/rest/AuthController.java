package com.example.chatapp.controller.rest;

import com.example.chatapp.model.dto.ApiResponse;
import com.example.chatapp.model.dto.auth.AuthResponse;
import com.example.chatapp.model.dto.auth.LoginRequest;
import com.example.chatapp.model.dto.auth.RegisterRequest;
import com.example.chatapp.model.dto.email_verification.SendVerificationCodeRequest;
import com.example.chatapp.model.dto.email_verification.VerificationResponse;
import com.example.chatapp.model.dto.email_verification.VerifyEmailRequest;
import com.example.chatapp.model.dto.refresh_token.RefreshTokenRequest;
import com.example.chatapp.service.AuthService;
import com.example.chatapp.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Email Verification", description = "API for email verification")
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registering new user: {}", request);
        AuthResponse response = authService.register(request);
        log.info("Registered new user: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        authService.logout(authentication.getName());
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/email-verification/send-code")
    public ResponseEntity<ApiResponse> sendCode(@Valid @RequestBody SendVerificationCodeRequest request) {
        emailVerificationService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok(new ApiResponse("Verification code sent to your email", true));
    }

    @PostMapping("/email-verification/verify")
    public ResponseEntity<VerificationResponse> verifyCode(@Valid @RequestBody VerifyEmailRequest request) {
        boolean verified = emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(new VerificationResponse(
                "Email successfully confirmed", true, verified));
    }

    @GetMapping("/email-verification/stats")
    public ResponseEntity<?> getStats(@RequestParam(required = false) String email) {
        return ResponseEntity.ok(emailVerificationService.getVerificationStats(email));
    }
}
