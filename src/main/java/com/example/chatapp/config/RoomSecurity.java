package com.example.chatapp.config;

import com.example.chatapp.handler.exception.RoomNotFoundException;
import com.example.chatapp.model.Room;
import com.example.chatapp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("roomSecurity")
@RequiredArgsConstructor
public class RoomSecurity {

    private final RoomRepository roomRepository;

    public boolean isOwner(Long roomId, String username) {
        Room room = roomRepository.findByIdWithOwner(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found: " + roomId));
        return room.getOwner().getUsername().equals(username);
    }
}