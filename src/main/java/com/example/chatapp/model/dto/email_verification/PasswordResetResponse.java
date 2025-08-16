package com.example.chatapp.model.dto.email_verification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordResetResponse {
    private String message;
    private boolean success;
    private String resetToken; // Temporary token for validation between steps (optional)
}