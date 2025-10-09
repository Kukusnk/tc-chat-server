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
@Schema(description = "Updated room information after a user joins the room")
public class RoomFullInfoDTO {
    private Long id;
    @Schema(description = "Room name", example = "general", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    private String description;
    private List<Topic> topics;
    private Long membersCount;
    private Long memberLimit;
    private String ownerName;
    private LocalDateTime createdAt;

    public static RoomFullInfoDTO fromEntity(Room room) {
        return RoomFullInfoDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .membersCount((long) room.getMembers().size())
                .memberLimit(room.getMemberLimit())
                .ownerName((room.getOwner() != null) ? room.getOwner().getUsername() : null)
                .createdAt(room.getCreatedAt())
                .build();
    }
}
