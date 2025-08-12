package com.example.chatapp.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "Username cannot be null or empty")
    @Size(min = 4, max = 16, message = "Username must be between 4 and 16")
    String username;
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email should be valid")
    String email;
    @NotBlank(message = "Password cannot be null or empty")
    @Size(min = 8, message = "Password must be greater than or equal to 8")
    String password;
}
