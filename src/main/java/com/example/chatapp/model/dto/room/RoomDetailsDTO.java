package com.example.chatapp.model.dto.room;

import com.example.chatapp.model.Room;
import com.example.chatapp.model.dto.message.MessageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Room information")
public class RoomDetailsDTO {
    private Long id;
    @Schema(description = "Room name", example = "general", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    private Long memberCount;
    private String ownerName;
    private List<MessageDTO> lastTenMessages = new ArrayList<>();

    public static RoomDetailsDTO fromEntity(Room room) {
        return RoomDetailsDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .memberCount((long) room.getMembers().size())
                .ownerName(room.getOwner().getUsername())
                .build();
    }
}
