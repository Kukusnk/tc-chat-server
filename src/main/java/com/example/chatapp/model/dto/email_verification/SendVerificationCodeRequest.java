package com.example.chatapp.model.dto.email_verification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "SendVerificationCodeRequest", description = "Request to send an email verification code")
public class SendVerificationCodeRequest {

    @Schema(description = "Email address to send verification code", example = "john@example.com")
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Incorrect email format")
    private String email;
}
