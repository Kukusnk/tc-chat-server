package com.example.chatapp.model.dto.room;

import com.example.chatapp.model.Topic;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Request to create a room")
@Builder
public class CreateRoomRequest {
    @NotBlank(message = "The room name cannot be empty")
    @Schema(description = "Room Name", example = "news", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 5, max = 128)
    private String name;

    @Size(max = 1000)
    private String description = "";
    @NotEmpty(message = "Topics list cannot be empty")
    private List<Topic> topics;

    @Min(value = 2)
    @Max(value = 100, message = "Maximum number of members = 100")
    private Long memberLimit;
}
