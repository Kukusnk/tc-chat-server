package com.example.chatapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Schema(description = "User typing status")
public class TypingStatus {
    @Schema(description = "User Name", example = "Charlie")
    String username;

    @Schema(description = "Flag indicating whether the user is printing", example = "true")
    boolean isTyping;
}
