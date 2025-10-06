package com.example.chatapp.service;

import com.example.chatapp.handler.exception.RoomNotFoundException;
import com.example.chatapp.handler.exception.TopicNotFoundException;
import com.example.chatapp.handler.exception.UserNotFoundException;
import com.example.chatapp.model.Room;
import com.example.chatapp.model.RoomType;
import com.example.chatapp.model.Topic;
import com.example.chatapp.model.User;
import com.example.chatapp.model.dto.room.CreateRoomRequest;
import com.example.chatapp.model.dto.room.CreateRoomResponse;
import com.example.chatapp.model.dto.room.RoomDTO;
import com.example.chatapp.model.dto.room.RoomListResponse;
import com.example.chatapp.repository.RoomRepository;
import com.example.chatapp.repository.TopicRepository;
import com.example.chatapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class RoomService {
    private static final Long MAX_ROOMS_PER_USER = 5L;
    private static final Long MAX_MEMBER_PER_ROOM = 100L;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    @Transactional
    public CreateRoomResponse createRoom(CreateRoomRequest request, Authentication authentication) {

        Long memberLimit = (request.getMemberLimit() == null || request.getMemberLimit() == 0)
                ? MAX_MEMBER_PER_ROOM
                : request.getMemberLimit();

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + authentication.getName()));

        List<Long> topicIds = request.getTopics().stream()
                .map(Topic::getId)
                .toList();

        List<Topic> validTopics = topicRepository.findAllById(topicIds);
        if (validTopics.size() != topicIds.size()) {
            throw new TopicNotFoundException("One or more topics do not exist");
        }

        Room room = Room
                .builder()
                .name(request.getName())
                .owner(user)
                .description(request.getDescription())
                .type(RoomType.DEFAULT_ROOM)
                .memberLimit(memberLimit)
                .members(new ArrayList<>(List.of(user)))
                .topics(request.getTopics())
                .createdAt(LocalDateTime.now())
                .build();

        Room savedRoom = roomRepository.save(room);
        log.info("Room created: id - {}, room name - {}, owner - {} ",
                savedRoom.getId(), savedRoom.getName(), user.getUsername());
        return CreateRoomResponse.fromEntity(savedRoom);
    }

    public List<RoomListResponse> getAllRooms() {
        return new ArrayList<>();
    }

    public RoomDTO getRoomById(Long id) {
        log.info("Get room by id - {}", id);
        Room room = roomRepository.findById(id).orElseThrow(() -> new RoomNotFoundException("Room id=" + id + " not found"));
        return RoomDTO.fromEntity(room);
    }

    @Transactional
    public boolean countByOwner(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + authentication.getName()));
        return roomRepository.countByOwner(user) < MAX_ROOMS_PER_USER;
    }

    @Transactional
    public void deleteRoomById(Long id) {
        log.info("Delete room by id - {}", id);
        roomRepository.deleteById(id);
    }

    @Transactional
    public void joinToRoom(Long id, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + authentication.getName()));

        Room room = roomRepository.findById(id)
                .map(r -> {
                    r.addMember(user);
                    return roomRepository.save(r);
                })
                .orElseThrow(() -> new RoomNotFoundException("Room id=" + id + " not found"));
    }
}
