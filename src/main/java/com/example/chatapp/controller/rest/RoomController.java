package com.example.chatapp.controller.rest;

import com.example.chatapp.model.Room;
import com.example.chatapp.model.dto.CreateRoomRequest;
import com.example.chatapp.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @Operation(summary = "Create a new room")
    @ApiResponse(responseCode = "200", description = "Room successfully created")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        log.info("Create a room with a name: {}", request.getName());
        Room room = roomService.createRoom(request.getName());
        return ResponseEntity.ok(room);
    }

    @GetMapping
    @Operation(summary = "Get a list of all rooms")
    public ResponseEntity<List<Room>> getAllRooms() {
        log.info("Request all rooms");
        //TODO through dto class, dont use entity
        List<Room> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a room by ID")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        log.info("Search for a room by ID: {}", id);
        return ResponseEntity.ok(roomService.getRoomById(id));
    }
}
