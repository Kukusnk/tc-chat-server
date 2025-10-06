package com.example.chatapp.model.dto.room;

import com.example.chatapp.model.Room;
import com.example.chatapp.model.dto.topic.TopicDTO;
import com.example.chatapp.util.DevTools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomSearchResponse {
    private Long id;
    private String name;
    private String description;
    private List<TopicDTO> topics;
    private Long memberLimit;
    private Integer membersCount;
    private LocalDateTime createdAt;

    public static RoomSearchResponse fromEntity(Room room) {
        return RoomSearchResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .topics(room.getTopics().stream().map(DevTools::topicsToDTO).collect(Collectors.toList()))
                .memberLimit(room.getMemberLimit())
                .membersCount(room.getMembers().size())
                .createdAt(room.getCreatedAt())
                .build();
    }
}
