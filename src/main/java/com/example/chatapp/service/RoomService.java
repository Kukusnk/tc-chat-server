package com.example.chatapp.service;

import com.example.chatapp.exception.RoomNotFoundException;
import com.example.chatapp.model.Room;
import com.example.chatapp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public Room createRoom(String name) {
        return roomRepository.save(new Room(null, name));
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Кімната не знайдена: " + id));
    }
}
