package com.example.chatapp.controller;

import com.example.chatapp.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class WebSocketController {

    private final MessageService messageService;

    public WebSocketController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/test")
    @SendTo("/topic/messages")
    public String processMessage(String message) {
        log.info("The message came through: {}", message);
        String answer = messageService.answerMessage(message);
        log.info("The answer is: {}", answer);
        return "{\"response\" : \"" + answer + "\"}";
    }
}
