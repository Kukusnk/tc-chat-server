package com.example.chatapp.model.dto.room;

import com.example.chatapp.model.Topic;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Request to create a room")
public class CreateRoomRequest {
    @NotBlank(message = "The room name cannot be empty")
    @Schema(description = "Room Name", example = "news", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 1000)
    private String description = "";
    @NotEmpty(message = "Topics list cannot be empty")
    private List<Topic> topics;

    @Size(max = 100, message = "Maximum number of members = 100")
    private Long memberLimit;
}
