package com.example.chatapp.model.dto.room;

import com.example.chatapp.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "All room members with name and id")
public class RoomMemberDTO {
    private Long id;
    private String username;

    public static RoomMemberDTO fromUser(User user) {
        return RoomMemberDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
