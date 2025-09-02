package com.example.chatapp.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "DeleteRequest", description = "Account deletion request containing the password for user confirmation")
public record DeleteRequest(
        @NotBlank
        String password
) {
}
