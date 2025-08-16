package com.example.chatapp.model.dto.email_verification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "The token cannot be empty")
    private String token;

    @NotBlank(message = "The password cannot be empty")
    @Size(min = 8, message = "Password must be greater than or equal to 8")
    private String newPassword;
}