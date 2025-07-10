package com.example.chatapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class Message {
    private Long id;
    private User sender;
    private String content;
    private LocalDateTime timestamp;
}
