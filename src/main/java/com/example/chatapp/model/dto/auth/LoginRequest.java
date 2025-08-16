package com.example.chatapp.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoginRequest", description = "Payload for user login")
public class LoginRequest {

    @Schema(description = "Username or email for login", example = "john_doe")
    @NotBlank(message = "Username or email must not be blank")
    private String usernameOrEmail;

    @Schema(description = "User password", example = "StrongPassword123")
    @NotBlank(message = "Password must not be blank")
    private String password;
}
