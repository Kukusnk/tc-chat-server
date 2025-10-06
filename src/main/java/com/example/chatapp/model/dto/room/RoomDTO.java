package com.example.chatapp.model.dto.room;

import com.example.chatapp.model.Room;
import com.example.chatapp.model.Topic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Room information")
public class RoomDTO {
    private Long id;
    @Schema(description = "Room name", example = "general", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    private String description;
    private List<Topic> topics;
    private Long memberLimit;
    private String ownerName;
    private LocalDateTime createdAt;

    public static RoomDTO fromEntity(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .topics(room.getTopics())
                .memberLimit(room.getMemberLimit())
                .ownerName(room.getOwner().getUsername())
                .createdAt(room.getCreatedAt())
                .build();
    }
}
