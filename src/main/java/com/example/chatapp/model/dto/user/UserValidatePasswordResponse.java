package com.example.chatapp.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UserValidatePasswordResponse")
public class UserValidatePasswordResponse {
    Boolean valid;
    String message;
}
