package com.example.chatapp.controller;

import com.example.chatapp.model.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Чат API працює");
    }


    /**
     *
     * @return List Messages
     */
    @GetMapping("/messages")
    public List<Message> getTestMessages() {
        return List.of(
                new Message("Htos'1", "Test mess", LocalDateTime.now()),
                new Message("Htos'2", "Another test", LocalDateTime.now())
        );
    }

}
