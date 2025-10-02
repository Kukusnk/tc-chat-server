package com.example.chatapp.controller.rest;

import com.example.chatapp.handler.exception.RoomOwnershipLimitExceededException;
import com.example.chatapp.model.Room;
import com.example.chatapp.model.dto.room.CreateRoomRequest;
import com.example.chatapp.model.dto.room.CreateRoomResponse;
import com.example.chatapp.model.dto.room.RoomListResponse;
import com.example.chatapp.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @Operation(
            summary = "Create a new chat room",
            description = """
                    Creates a new chat room with the provided parameters. 
                    - User can create up to 5 chat rooms. 
                    - Chat room name must be unique (5–128 characters). 
                    - Description is optional (up to 1000 characters). 
                    - The creator is automatically added as the first member of the room.
                    - If member limit is not specified, the default maximum (100) will apply.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateRoomResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data (validation failed)",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User is not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User has reached the maximum number of owned rooms",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Room with the same name already exists",
                    content = @Content)
    })
    @Transactional
    public ResponseEntity<CreateRoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request,
                                                         @Parameter(hidden = true) Authentication authentication) {
        log.info("User {} is creating room with name: {}",
                authentication.getName(), request.getName());
        if (!roomService.countByOwner(authentication)) {
            log.warn("User {} have 5 or above rooms owned", authentication.getName());
            throw new RoomOwnershipLimitExceededException("User " + authentication.getName()
                    + " has reached the maximum allowed number of owned rooms (5).");
        }
        CreateRoomResponse room = roomService.createRoom(request, authentication);
        return ResponseEntity.ok(room);
    }

    @GetMapping
    @Operation(summary = "Get a list of all rooms")
    public ResponseEntity<List<RoomListResponse>> getAllRooms() {
        log.info("Request all rooms");
        //TODO through dto class, dont use entity
        List<RoomListResponse> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a room by ID")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        log.info("Search for a room by ID: {}", id);
        return ResponseEntity.ok(roomService.getRoomById(id));
    }
}
