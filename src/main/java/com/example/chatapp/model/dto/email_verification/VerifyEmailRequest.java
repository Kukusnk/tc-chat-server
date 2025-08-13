package com.example.chatapp.model.dto.email_verification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyEmailRequest {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Incorrect email format")
    private String email;

    @NotBlank(message = "The code cannot be empty")
    @Pattern(regexp = "\\d{6}", message = "The code must contain 6 digits")
    private String code;
}
