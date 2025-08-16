package com.example.chatapp.model.dto.email_verification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(name = "VerifyEmailRequest", description = "Request to verify email with a code")
public class VerifyEmailRequest {

    @Schema(description = "Email address to verify", example = "john@example.com")
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Incorrect email format")
    private String email;

    @Schema(description = "6-digit verification code", example = "123456")
    @NotBlank(message = "The code cannot be empty")
    @Pattern(regexp = "\\d{6}", message = "The code must contain 6 digits")
    private String code;
}
