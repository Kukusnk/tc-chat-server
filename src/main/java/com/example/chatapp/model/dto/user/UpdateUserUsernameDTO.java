package com.example.chatapp.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "UpdateUserUsernameDTO", description = "Username update request")
public record UpdateUserUsernameDTO(
        @NotBlank
        @Size(min = 6, max = 15)
        String username
) {
}
