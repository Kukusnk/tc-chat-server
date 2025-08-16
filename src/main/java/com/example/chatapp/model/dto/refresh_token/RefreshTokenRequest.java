package com.example.chatapp.model.dto.refresh_token;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RefreshTokenRequest", description = "Payload for refreshing authentication token")
public class RefreshTokenRequest {

    @Schema(description = "Valid refresh token", example = "jwt-refresh-token")
    @NotBlank
    private String refreshToken;
}
