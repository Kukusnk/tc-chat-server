package com.example.chatapp.model.dto.email_verification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(name = "PasswordResetResponse", description = "Response for password reset operations")
@Data
@AllArgsConstructor
public class PasswordResetResponse {
    @Schema(description = "Message with operation details", example = "The password has been successfully changed.")
    private String message;

    @Schema(description = "Flag indicating whether the operation succeeded", example = "true")
    private boolean success;

    @Schema(description = "Temporary reset token (only for verify step, otherwise null)",
            example = "eyJhbGciOiJIUzI1NiJ9...", nullable = true)
    private String resetToken;
}