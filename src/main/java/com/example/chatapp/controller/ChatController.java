package com.example.chatapp.controller;

import com.example.chatapp.model.Message;
import com.example.chatapp.model.User;
import com.example.chatapp.model.dto.MessageDTO;
import com.example.chatapp.model.dto.SendMessageDTO;
import com.example.chatapp.util.DevTools;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "Chat methods")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    static List<Message> testMessages = new ArrayList<>();

    static {
        Message mes1 = Message.builder()
                .id(1L)
                .sender(new User(1L, "User1"))
                .content("This is a first simple chat message")
                .timestamp(LocalDateTime.now())
                .build();
        Message mes2 = Message.builder()
                .id(2L)
                .sender(new User(2L, "User2"))
                .content("This is a second simple chat message")
                .timestamp(LocalDateTime.now())
                .build();
        testMessages.add(mes1);
        testMessages.add(mes2);
    }

    @Operation(
            summary = "Test endpoint for check connection"
    )

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Чат API працює");
    }


    @Operation(
            summary = "Test endpoint to receive all messages",
            description = "Returns a regular list of pre-prepared objects"
    )
    @GetMapping("/test/messages")
    public List<MessageDTO> getTestMessages() {
        return new ArrayList<>(testMessages.stream().map(DevTools::messageToDTO).toList());
    }

    @Operation(
            summary = "Test endpoint for sending a message",
            description = "Generates numeric id, Receives DTO messages " +
                    "and saves the entity to the local test list"

    )
    @PostMapping("/test/sendMessage")
    public ResponseEntity<SendMessageDTO> sendMessage(@RequestBody SendMessageDTO messageDTO) {
        Long currentId = DevTools.getLastMessageID(testMessages) + 1;
        Message message = Message.builder()
                .id(currentId)
                .sender(new User(currentId, messageDTO.getSenderNickname()))
                .content(messageDTO.getContent())
                .timestamp(LocalDateTime.now())
                .build();
        testMessages.add(message);
        return ResponseEntity.ok(messageDTO);
    }

}
