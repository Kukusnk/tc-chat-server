package com.example.chatapp.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "UpdateUserPasswordDTO", description = "Password update request")
public record UpdateUserPasswordDTO(
        @NotBlank
        @Size(min = 8)
        String password
) {
}
