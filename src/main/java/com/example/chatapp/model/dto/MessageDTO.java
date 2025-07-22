package com.example.chatapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Schema(description = "Saved message with a timestamp")
public class MessageDTO {
    @NotBlank
    @Schema(description = "Sender's name", example = "Alice", requiredMode = Schema.RequiredMode.REQUIRED)
    String sender;

    @NotBlank
    @Schema(description = "Message content", example = "Hello everyone!", requiredMode = Schema.RequiredMode.REQUIRED)
    String content;

    @Schema(description = "Dispatch time", example = "2025-07-15T14:30:00")
    LocalDateTime timestamp;
}
