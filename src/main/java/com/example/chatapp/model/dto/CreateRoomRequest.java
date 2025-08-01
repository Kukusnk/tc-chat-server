package com.example.chatapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to create a room")
public class CreateRoomRequest {
    @NotBlank(message = "The room name cannot be empty")
    @Schema(description = "Room Name", example = "news", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}
