package com.example.chatapp.model.dto.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Username when joining a room")
public class JoinIntoRoomResponse {
    @Schema(description = "User Name", example = "Bob", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
}
