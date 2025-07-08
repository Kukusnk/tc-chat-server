package com.example.chatapp.model.dto;

import com.example.chatapp.model.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageDTO {
    String senderNickname;
    String content;
    LocalDateTime timestamp;
}
