package com.example.chatapp.controller.rest;

import com.example.chatapp.model.dto.email_verification.ForgotPasswordRequest;
import com.example.chatapp.model.dto.email_verification.PasswordResetResponse;
import com.example.chatapp.model.dto.email_verification.ResetPasswordRequest;
import com.example.chatapp.model.dto.email_verification.VerifyResetCodeRequest;
import com.example.chatapp.service.ResetPasswordService;
import com.example.chatapp.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Password Reset", description = "API for password reset")
public class ResetPasswordController {

    private final ResetPasswordService passwordResetService;
    private final JwtUtil jwtUtil;

    @PostMapping("/forgot-password")
    @Operation(summary = "Send password reset code to email")
    public ResponseEntity<PasswordResetResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        passwordResetService.sendPasswordResetCode(request.getEmail());

        return ResponseEntity.ok(new PasswordResetResponse(
                "If an account with this email exists, a password reset code has been sent to you",
                true,
                null
        ));
    }

    @PostMapping("/verify-reset-code")
    @Operation(summary = "Check password reset code")
    public ResponseEntity<PasswordResetResponse> verifyResetCode(
            @Valid @RequestBody VerifyResetCodeRequest request) {

        boolean isValid = passwordResetService.verifyResetCode(request.getEmail(), request.getCode());

        String resetToken = null;
        if (isValid) {
            resetToken = jwtUtil.generateResetToken(request.getEmail());
        }
        return ResponseEntity.ok(new PasswordResetResponse(
                isValid ? "The code is valid. You can set a new password" : "Incorrect code",
                isValid,
                resetToken
        ));
    }

    @PostMapping("reset-password")
    @Operation(summary = "Set a new password")
    public ResponseEntity<PasswordResetResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        if (!jwtUtil.validateResetToken(request.getToken())) {
            return ResponseEntity.badRequest().body(new PasswordResetResponse(
                    "Invalid or expired reset token",
                    false,
                    null
            ));
        }

        String email = jwtUtil.extractEmailFromResetToken(request.getToken());

        passwordResetService.resetPassword(
                email,
                request.getNewPassword()
        );
//        passwordResetService.resetPassword(
//                request.getEmail(),
//                request.getCode(),
//                request.getNewPassword()
//        );

        return ResponseEntity.ok(new PasswordResetResponse(
                "The password has been successfully changed. All active sessions are terminated.",
                true,
                null
        ));
    }
}
