package com.example.chatapp.model.dto.email_verification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO to send code
@Data
public class SendVerificationCodeRequest {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Incorrect email format")
    private String email;
}
