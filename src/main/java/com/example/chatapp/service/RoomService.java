package com.example.chatapp.service;

import com.example.chatapp.handler.exception.RoomNotFoundException;
import com.example.chatapp.handler.exception.RoomUniqueException;
import com.example.chatapp.model.Room;
import com.example.chatapp.model.dto.room.CreateRoomRequest;
import com.example.chatapp.model.dto.room.CreateRoomResponse;
import com.example.chatapp.repository.RoomRepository;
import com.example.chatapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public CreateRoomResponse createRoom(CreateRoomRequest request, String username) {

        if (roomRepository.existsByName(username)) {
            throw new RoomUniqueException("A room named '" + request.getName() + "' exists");
        }
        if (request.getMemberLimit() == 0) {
            request.setMemberLimit(100);
        }
        return new CreateRoomResponse();
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Кімната не знайдена: " + id));
    }
}
