package com.example.chatapp.controller;

import com.example.chatapp.model.dto.CreateRoomRequest;
import com.example.chatapp.model.Room;
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
    @ApiResponse(responseCode = "200", description = "Кімната успішно створена")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        log.info("Create a room with a name: {}", request.getName());
        Room room = roomService.createRoom(request.getName());
        return ResponseEntity.ok(room);
    }

    @GetMapping
    @Operation(summary = "Get a list of all rooms")
    public ResponseEntity<List<Room>> getAllRooms() {
        log.info("Запит всіх кімнат");
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отримати кімнату за ID")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        log.info("Пошук кімнати з ID: {}", id);
        return ResponseEntity.ok(roomService.getRoomById(id));
    }
}
