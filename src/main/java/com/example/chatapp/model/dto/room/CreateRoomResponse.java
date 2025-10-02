package com.example.chatapp.model.dto.room;

import com.example.chatapp.model.Room;
import com.example.chatapp.model.Topic;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CreateRoomResponse {
    private Long id;
    private String name;
    private String description;
    private List<Topic> topics;
    private Long memberLimit;
    private Long ownerId;
    private LocalDateTime createdAt;

    public static CreateRoomResponse fromEntity(Room room) {
        return CreateRoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .topics(room.getTopics())
                .memberLimit(room.getMemberLimit())
                .ownerId(room.getOwner().getId())
                .createdAt(room.getCreatedAt())
                .build();
    }
}
