package com.example.chatapp.model.dto.email_verification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(name = "ResetPasswordRequest", description = "Request to set a new password")
@Data
public class ResetPasswordRequest {
    @Schema(description = "Temporary reset token issued after code verification",
            example = "eyJhbGciOiJIUzI1NiJ9...")
    @NotBlank(message = "The token cannot be empty")
    private String token;

    @Schema(description = "New user password (at least 8 characters)", example = "StrongPassword123")
    @NotBlank(message = "The password cannot be empty")
    @Size(min = 8, message = "Password must be greater than or equal to 8")
    private String newPassword;
}