package com.example.chatapp.model.dto.room;

import com.example.chatapp.model.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Preview room information for room list")
public class RoomPreviewDTO {
    @Schema(description = "Room ID", example = "1")
    Long id;
    @Schema(description = "Room name", example = "General Chat")
    String name;
    @Schema(description = "Shortened description")
    String shortDescription;
    @Schema(description = "Content of the last message")
    String lastMessage;
    @Schema(description = "Number of participants")
    Long participantsCount;

    public static RoomPreviewDTO fromRoom(Room room) {
        String shortDesc = (room.getDescription() != null && !room.getDescription().isEmpty())
                ? room.getDescription().substring(0, Math.min(50, room.getDescription().length())).concat("...")
                : "";

        String lastMsg = (!room.getMessages().isEmpty())
                ? room.getMessages().get(room.getMessages().size() - 1).getContent()
                : "";
        return RoomPreviewDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .shortDescription(shortDesc)
                .lastMessage(lastMsg)
                .participantsCount((long) room.getMembers().size())
                .build();
    }
}
