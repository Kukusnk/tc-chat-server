package com.example.chatapp.model.dto.avatar;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "Avatar Response", description = "Avatar dto who contains message and avatar url")
public class AvatarResponse {
    String message;
    String avatarUrl;
}
