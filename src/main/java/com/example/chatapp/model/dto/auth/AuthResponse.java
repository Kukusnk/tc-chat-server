package com.example.chatapp.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "AuthResponse", description = "Authentication response containing tokens")
public class AuthResponse {

    @Schema(description = "JWT access token", example = "jwt-access-token")
    private String accessToken;

    @Schema(description = "JWT refresh token", example = "jwt-refresh-token")
    private String refreshToken;

    @Schema(description = "Type of token", example = "Bearer")
    private String tokenType;
}
