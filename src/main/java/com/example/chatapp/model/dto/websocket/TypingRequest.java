package com.example.chatapp.model.dto.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "A query indicating that the user is typing")
public class TypingRequest {
    @Schema(description = "Sender's name", example = "Charlie", requiredMode = Schema.RequiredMode.REQUIRED)
    String senderName;
}