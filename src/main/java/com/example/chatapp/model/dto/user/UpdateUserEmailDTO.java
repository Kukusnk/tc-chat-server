package com.example.chatapp.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(name = "UpdateUserEmailDTO", description = "Request containing email")
public record UpdateUserEmailDTO(
        @Schema(description = "New email", example = "bohdantaran@gmail.com")
        @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        String email
) {
}
