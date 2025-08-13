package com.example.chatapp.controller.websocket;

import com.example.chatapp.model.dto.message.MessageDTO;
import com.example.chatapp.model.dto.message.SendMessageDTO;
import com.example.chatapp.model.dto.websocket.JoinIntoRoomRequest;
import com.example.chatapp.model.dto.websocket.JoinIntoRoomResponse;
import com.example.chatapp.model.dto.websocket.TypingRequest;
import com.example.chatapp.model.dto.websocket.TypingStatus;
import com.example.chatapp.service.MessageService;
import io.github.springwolf.bindings.stomp.annotations.StompAsyncOperationBinding;
import io.github.springwolf.core.asyncapi.annotations.AsyncListener;
import io.github.springwolf.core.asyncapi.annotations.AsyncMessage;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class WebSocketController {

    private final MessageService messageService;

    @Autowired
    public WebSocketController(MessageService messageService) {
        this.messageService = messageService;
    }


    @MessageMapping("/test")
    @SendTo("/topic/messages")
    @AsyncListener(
            operation = @AsyncOperation(
                    channelName = "/app/test",
                    description = "Test WebSocket message echo endpoint.",
                    payloadType = String.class,
                    message = @AsyncMessage(
                            name = "Simple string",
                            description = "Message, that the client sends",
                            contentType = "application/json"
                    )
            )
    )
    @AsyncPublisher(
            operation = @AsyncOperation(
                    channelName = "/topic/messages",
                    description = "Server → client: Receives a test message and sends back a random string of the same length.",
                    payloadType = String.class,
                    message = @AsyncMessage(
                            name = "Pseudo json message",
                            description = "Json message, that the server gives to the subscriber",
                            contentType = "application/json"
                    )
            )
    )
    @StompAsyncOperationBinding
    public String processMessage(String message) {
        String answer = messageService.answerMessage(message);
        return "{\"response\" : \"" + answer + "\"}";
    }

    @MessageMapping("/room/{roomId}")
    @SendTo("/topic/room/{roomId}")
    @AsyncListener(
            operation = @AsyncOperation(
                    channelName = "/app/room/{roomId}",
                    description = "Client → server: sending a message to a room",
                    payloadType = SendMessageDTO.class,
                    message = @AsyncMessage(
                            name = "Incoming message",
                            description = "Message, that the client sends",
                            contentType = "application/json"
                    )
            )
    )
    @AsyncPublisher(
            operation = @AsyncOperation(
                    channelName = "/topic/room/{roomId}",
                    description = "Server → clients: send a message to everyone in the room",
                    payloadType = MessageDTO.class,
                    message = @AsyncMessage(
                            name = "Sending messages",
                            description = "Message, that the server gives to the subscribers",
                            contentType = "application/json"
                    )
            )
    )
    @StompAsyncOperationBinding
    public MessageDTO sendMessage(@DestinationVariable Long roomId, SendMessageDTO request) {
        log.info("Message in room {} from {}: {}", roomId, request.getSender(), request.getContent());
        return messageService.saveAndReturn(roomId, request);
    }

    @MessageMapping("/typing/{roomId}")
    @SendTo("/topic/typing/{roomId}")
    @AsyncListener(
            operation = @AsyncOperation(
                    channelName = "/app/typing/{roomId}",
                    description = "Client → server: sends a signal that the user is entering something in the room",
                    payloadType = TypingRequest.class,
                    message = @AsyncMessage(
                            name = "Typing request",
                            description = "A query containing the name of a user who writes something",
                            contentType = "application/json"
                    )
            )
    )
    @AsyncPublisher(
            operation = @AsyncOperation(
                    channelName = "/topic/typing/{roomId}",
                    description = "Server → clients: sends the name of the user who is typing to everyone in the room",
                    payloadType = TypingStatus.class,
                    message = @AsyncMessage(
                            name = "Typing status",
                            description = "Typing status containing name and flag true",
                            contentType = "application/json"
                    )
            )
    )
    @StompAsyncOperationBinding
    public TypingStatus personTyping(@DestinationVariable Long roomId, TypingRequest request) {
        return TypingStatus.builder()
                .username(request.getSenderName())
                .isTyping(true)
                .build();
    }


    @MessageMapping("/join/{roomId}")
    @SendTo("/topic/room/{roomId}/joined")
    @AsyncListener(
            operation = @AsyncOperation(
                    channelName = "/app/join/{roomId}",
                    description = "Client → server: sends a signal that the user is join in the room",
                    payloadType = JoinIntoRoomRequest.class,
                    message = @AsyncMessage(
                            name = "Join into room request",
                            description = "String containing the name of the user that enters the room",
                            contentType = "application/json"
                    )
            )
    )
    @AsyncPublisher(
            operation = @AsyncOperation(
                    channelName = "/topic/room/{roomId}/joined",
                    description = "Server → clients: sends the name of the user who is typing to everyone in the room",
                    payloadType = JoinIntoRoomResponse.class,
                    message = @AsyncMessage(
                            name = "Join into room response",
                            description = "Response containing the name of the user who entered the room",
                            contentType = "application/json"
                    )
            )
    )
    @StompAsyncOperationBinding
    public JoinIntoRoomResponse joinIntoRoom(@DestinationVariable Long roomId, JoinIntoRoomRequest username) {
        log.info("{} joined room {}", username, roomId);
        return JoinIntoRoomResponse.builder()
                .username(username.getUsername())
                .build();
    }
}
