package com.example.chatapp.controller.rest;

import com.example.chatapp.handler.exception.RoomOwnershipLimitExceededException;
import com.example.chatapp.model.dto.room.*;
import com.example.chatapp.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<CreateRoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request,
                                                         @Parameter(hidden = true) Authentication authentication) {
        log.info("User {} is creating room with name: {}",
                authentication.getName(), request.getName());
        if (!roomService.hasOwnershipLimitReached(authentication)) {
            log.warn("User {} have 5 or above rooms owned", authentication.getName());
            throw new RoomOwnershipLimitExceededException("User " + authentication.getName()
                    + " has reached the maximum allowed number of owned rooms (5).");
        }
        CreateRoomResponse room = roomService.createRoom(request, authentication);
        return ResponseEntity.ok(room);
    }

    @GetMapping
    @Operation(summary = "Get a list of all available rooms", // ADDED: more precise
            description = "Returns paginated list of rooms, sorted by creation date descending.")
    @ApiResponses(value = { // ADDED
            @ApiResponse(responseCode = "200", description = "List of rooms returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "User is not authenticated",
                    content = @Content)
    })
    public ResponseEntity<Page<RoomPreviewDTO>> getAllRooms(
            @ParameterObject @Parameter(description = "Pagination and sorting parameters") Pageable pageable
    ) {
        log.info("Request all rooms");
        Page<RoomPreviewDTO> rooms = roomService.getAllRooms(pageable);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room details by ID",
            description = "Returns room information including last 10 messages.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room information returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoomDetailsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Room not found",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User is not authenticated",
                    content = @Content)
    })
    public ResponseEntity<RoomDetailsDTO> getRoomById(@PathVariable @Parameter(description = "Room ID") Long id) {
        log.info("Search for a room by ID: {}", id);
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@roomSecurity.isOwner(#id, authentication.name)")
    @Operation(summary = "Delete room by ID",
            description = "Only room owner can delete the room.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Room removed", content = @Content),
            @ApiResponse(responseCode = "404", description = "Room not found",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User is not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not the owner",
                    content = @Content)
    })
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long id) {
        log.info("Delete a room by ID: {}", id);
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/join")
    @Operation(summary = "Join a room by ID", // ADDED
            description = "User joins the room if not full and not already a member.")
    @ApiResponses(value = { // ADDED
            @ApiResponse(responseCode = "200", description = "Room info after join",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoomFullInfoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Room is full or user already member",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Room not found",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User is not authenticated",
                    content = @Content)
    })
    public ResponseEntity<RoomFullInfoDTO> joinRoom(@PathVariable Long id,
                                                    @Parameter(hidden = true) Authentication authentication) {
        log.info("Join a room by ID: {}", id);
        RoomFullInfoDTO room = roomService.joinToRoom(id, authentication);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/{id}/leave")
    @Operation(summary = "Leave a room by ID", // ADDED
            description = "User leaves the room. If owner, schedules deletion after 7 days.")
    @ApiResponses(value = { // ADDED
            @ApiResponse(responseCode = "204", description = "User left the room", content = @Content),
            @ApiResponse(responseCode = "400", description = "User not in room",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Room not found",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User is not authenticated",
                    content = @Content)
    })
    public ResponseEntity<Void> leaveRoom(@PathVariable Long id,
                                          @Parameter(hidden = true) Authentication authentication) {
        log.info("Leave a room by ID: {}", id);
        roomService.leaveRoom(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    @Operation(summary = "Get room members", // ADDED
            description = "Returns list of members for the room. User must be a member.")
    @ApiResponses(value = { // ADDED
            @ApiResponse(responseCode = "200", description = "List of members",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoomMemberDTO.class))),
            @ApiResponse(responseCode = "400", description = "User not in room",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Room not found",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User is not authenticated",
                    content = @Content)
    })
    @PreAuthorize("@roomSecurity.isRoomMember(#id, authentication.name)")
    public ResponseEntity<List<RoomMemberDTO>> getMembers(@PathVariable Long id,
                                                          @Parameter(hidden = true) Authentication authentication) {
        log.info("Get members of a room by ID: {}", id);
        return ResponseEntity.ok(roomService.getRoomMembers(id, authentication));
    }

    @PostMapping("/{id}/become-owner")
    @Operation(summary = "Become room owner", // ADDED: more precise
            description = "Current member becomes owner (e.g., if previous owner left).")
    @ApiResponses(value = { // ADDED
            @ApiResponse(responseCode = "200", description = "Ownership claimed", content = @Content),
            @ApiResponse(responseCode = "400", description = "Ownership already claimed by another",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not a member",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Room not found",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User is not authenticated",
                    content = @Content)
    })
    @PreAuthorize("@roomSecurity.isRoomMember(#id, authentication.name)")
    public ResponseEntity<Void> becomeOwner(@PathVariable Long id,
                                            @Parameter(hidden = true) Authentication authentication) {
        roomService.becomeOwner(id, authentication);
        return ResponseEntity.ok().build();
    }
}
