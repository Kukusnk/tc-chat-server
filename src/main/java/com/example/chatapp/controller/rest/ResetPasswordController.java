package com.example.chatapp.controller.rest;

import com.example.chatapp.model.dto.email_verification.ForgotPasswordRequest;
import com.example.chatapp.model.dto.email_verification.PasswordResetResponse;
import com.example.chatapp.model.dto.email_verification.ResetPasswordRequest;
import com.example.chatapp.model.dto.email_verification.VerifyResetCodeRequest;
import com.example.chatapp.service.ResetPasswordService;
import com.example.chatapp.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
            summary = "Send password reset code to email",
            description = """
                    Sends a password reset code to the user's email.
                    
                    Possible error responses:
                    - 400: Validation errors (invalid email format, code already sent, failed to send email)
                    - 405: Method not supported
                    - 500: Internal server error
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reset code sent (if account exists)",
                    content = @Content(schema = @Schema(implementation = PasswordResetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "Email cannot be empty"),
                                    @ExampleObject(name = "Spam protection", value = "The code has already been sent. Wait a moment before resending."),
                                    @ExampleObject(name = "Failed sending", value = "Failed to send password reset code")
                            })),
            @ApiResponse(responseCode = "405", description = "Method not allowed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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
    @Operation(
            summary = "Check password reset code",
            description = """
                    Verifies the provided reset code. If valid, returns a temporary reset token.
                    
                    Possible error responses:
                    - 400: Invalid/expired code, validation errors
                    - 405: Method not supported
                    - 500: Internal server error
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reset code verified",
                    content = @Content(schema = @Schema(implementation = PasswordResetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Invalid code", value = "Invalid or expired password reset code"),
                                    @ExampleObject(name = "Validation error", value = "The code must contain 6 digits")
                            })),
            @ApiResponse(responseCode = "405", description = "Method not allowed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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
    @Operation(
            summary = "Set a new password",
            description = """
                    Sets a new password using a valid reset token.
                    
                    Possible error responses:
                    - 400: Invalid or expired reset token, validation errors, failed to reset password
                    - 405: Method not supported
                    - 500: Internal server error
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password successfully reset",
                    content = @Content(schema = @Schema(implementation = PasswordResetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Invalid token", value = "Invalid or expired reset token"),
                                    @ExampleObject(name = "Validation error", value = "Password must be greater than or equal to 8"),
                                    @ExampleObject(name = "Failed reset", value = "Failed to reset the password")
                            })),
            @ApiResponse(responseCode = "405", description = "Method not allowed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

        return ResponseEntity.ok(new PasswordResetResponse(
                "The password has been successfully changed. All active sessions are terminated.",
                true,
                null
        ));
    }
}
