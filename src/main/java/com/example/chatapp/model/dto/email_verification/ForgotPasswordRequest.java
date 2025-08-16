package com.example.chatapp.model.dto.email_verification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "ForgotPasswordRequest", description = "Request for sending a password reset code")
public class ForgotPasswordRequest {
    @Schema(description = "User email", example = "john@example.com")
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Incorrect email format")
    private String email;
}
