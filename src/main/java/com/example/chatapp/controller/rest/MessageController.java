package com.example.chatapp.controller.rest;

import com.example.chatapp.handler.exception.RoomNotFoundException;
import com.example.chatapp.model.dto.message.MessageDTO;
import com.example.chatapp.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Messages", description = "Operations for fetching chat messages by room")
@RestController
@RequestMapping("/api")
public class MessageController {
    private final static Logger log = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(
            summary = "Health check",
            description = "Simple endpoint to verify that the Chat API is up and running",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "API is operational",
                            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string", example = "API chat is working"))
                    )
            }
    )
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("API chat is working");
    }

    @Operation(
            summary = "Get messages by room ID",
            description = "Returns the full list of messages for the specified chat room, ordered by timestamp ascending.",
            parameters = {
                    @Parameter(
                            name = "roomId",
                            description = "Unique identifier of the chat room",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", example = "123")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of messages in the room",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = MessageDTO.class)))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Room with given ID not found",
                            content = @Content(schema = @Schema(type = "string", example = "Room not found"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(type = "string", example = "Unexpected error"))
                    )
            }
    )
    @GetMapping("/{room_id}/messages")
    public ResponseEntity<List<MessageDTO>> getMessage(@PathVariable Long room_id) {
        List<MessageDTO> messages;
        try {
            messages = messageService.getAllMessagesByRoomId(room_id);
        } catch (RoomNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(messages);
    }
}
