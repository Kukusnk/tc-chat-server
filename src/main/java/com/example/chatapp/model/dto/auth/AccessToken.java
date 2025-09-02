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
@Schema(name = "AccessToken", description = "Jwt token on username change")
public class AccessToken {
    @Schema(description = "JWT access token", example = "jwt-access-token-tralala")
    private String accessToken;

    @Schema(description = "Type of token", example = "Bearer")
    private String tokenType;
}
