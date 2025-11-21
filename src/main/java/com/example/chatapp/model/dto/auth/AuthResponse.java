package com.example.chatapp.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accessToken;

    @Schema(description = "JWT refresh token", example = "jwt-refresh-token")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;

    @Schema(description = "Type of token", example = "Bearer")
    private String tokenType;
}
