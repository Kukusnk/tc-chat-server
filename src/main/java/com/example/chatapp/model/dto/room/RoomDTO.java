package com.example.chatapp.model.dto.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Room information")
public class RoomDTO {
    @Schema(description = "Room name", example = "general", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}
