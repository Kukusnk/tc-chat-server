package com.example.chatapp.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "RegisterRequest", description = "Payload for registering a new user")
public class RegisterRequest {

    @Schema(description = "Unique username for the user", example = "john_doe")
    @NotBlank(message = "Username cannot be null or empty")
    @Size(min = 4, max = 16, message = "Username must be between 4 and 16")
    private String username;

    @Schema(description = "Valid email address", example = "john@example.com")
    @NotBlank(message = "Email cannot be null or empty")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email should be valid")
    private String email;

    @Schema(description = "Password with minimum 8 characters", example = "StrongPassword123")
    @NotBlank(message = "Password cannot be null or empty")
    @Size(min = 8, message = "Password must be greater than or equal to 8")
    private String password;
}
