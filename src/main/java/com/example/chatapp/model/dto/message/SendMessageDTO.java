package com.example.chatapp.model.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to send a message")
public class SendMessageDTO {
    @NotBlank
    @Schema(description = "Sender's name", example = "Alice", requiredMode = Schema.RequiredMode.REQUIRED)
    String sender;

    @NotBlank
    @Schema(description = "Message content", example = "Hello everyone!", requiredMode = Schema.RequiredMode.REQUIRED)
    String content;
}