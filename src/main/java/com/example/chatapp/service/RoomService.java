package com.example.chatapp.service;

import com.example.chatapp.handler.exception.RoomLimitMembersException;
import com.example.chatapp.handler.exception.RoomNotFoundException;
import com.example.chatapp.handler.exception.TopicNotFoundException;
import com.example.chatapp.handler.exception.UserNotFoundException;
import com.example.chatapp.model.Room;
import com.example.chatapp.model.RoomType;
import com.example.chatapp.model.Topic;
import com.example.chatapp.model.User;
import com.example.chatapp.model.dto.message.MessageDTO;
import com.example.chatapp.model.dto.room.*;
import com.example.chatapp.repository.MessageRepository;
import com.example.chatapp.repository.RoomRepository;
import com.example.chatapp.repository.TopicRepository;
import com.example.chatapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class RoomService {
    private static final Long MAX_ROOMS_PER_USER = 5L;
    private static final Long MAX_MEMBER_PER_ROOM = 100L;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public CreateRoomResponse createRoom(CreateRoomRequest request, Authentication authentication) {

        Long memberLimit = (request.getMemberLimit() == null || request.getMemberLimit() == 0)
                ? MAX_MEMBER_PER_ROOM
                : Math.min(request.getMemberLimit(), MAX_MEMBER_PER_ROOM);

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + authentication.getName()));

        List<Long> topicIds = request.getTopics().stream()
                .map(Topic::getId)
                .filter(Objects::nonNull)
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
                .topics(validTopics)
                .createdAt(LocalDateTime.now())
                .build();

        Room savedRoom = roomRepository.save(room);
        log.info("Room created: id - {}, room name - {}, owner - {} ",
                savedRoom.getId(), savedRoom.getName(), user.getUsername());
        return CreateRoomResponse.fromEntity(savedRoom);
    }

    public Page<RoomPreviewDTO> getAllRooms(Pageable pageable) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return roomRepository.findAvailableRooms(sorted)
                .map(RoomPreviewDTO::fromRoom);
    }

    public RoomDetailsDTO getRoomById(Long id) {
        log.info("Get room by id - {}", id);
        Room room = roomRepository.findById(id).orElseThrow(() -> new RoomNotFoundException("Room id=" + id + " not found"));
        RoomDetailsDTO dto = RoomDetailsDTO.fromEntity(room);
        dto.setLastTenMessages(messageRepository.findTop10ByRoomIdOrderByTimestampDesc(id)
                .stream()
                .map(MessageDTO::fromMessage)
                .toList());
        return dto;
    }

    @Transactional
    public boolean hasOwnershipLimitReached(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + authentication.getName()));
        return roomRepository.countByOwner(user) < MAX_ROOMS_PER_USER;
    }

    @Transactional
    public void deleteRoomById(Long id) {
        log.info("Delete room by id - {}", id);
        messageRepository.deleteByRoomId(id);
        roomRepository.deleteById(id);
    }

    @Transactional
    public RoomFullInfoDTO joinToRoom(Long id, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + authentication.getName()));

        Room room = roomRepository.findById(id)
                .map(r -> {
                    if (r.getMembers().size() >= r.getMemberLimit())
                        throw new RoomLimitMembersException("Room is already full");
                    if (r.getMembers().contains(user)) return r;
                    r.addMember(user);
                    return roomRepository.save(r);
                })
                .orElseThrow(() -> new RoomNotFoundException("Room id=" + id + " not found"));
        log.info("User {} joined to room: id - {}, room name - {}", user.getUsername(), id, room.getName());
        return RoomFullInfoDTO.fromEntity(room);
    }

    @Transactional
    public void leaveRoom(Long id, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + authentication.getName()));

        Room room = roomRepository.findById(id)
                .map(r -> {
                    if (!r.getMembers().contains(user))
                        throw new UserNotFoundException("User not in room");
                    if (r.getOwner().equals(user)) {
                        r.setOwner(null);
                        r.setDeleteAfter(LocalDateTime.now().plusDays(7));
                    }
                    r.removeMember(user);
                    return roomRepository.save(r);
                })
                .orElseThrow(() -> new RoomNotFoundException("Room id=" + id + " not found"));
        log.info("User {} out room: id - {}, room name - {}", user.getUsername(), id, room.getName());
    }

    public List<RoomMemberDTO> getRoomMembers(Long id, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + authentication.getName()));
        log.info("User {} get room members - {}", user.getUsername(), id);
        return userRepository.findMembersByRoomId(id)
                .stream()
                .map(RoomMemberDTO::fromUser)
                .toList();
    }

    @Transactional
    public void becomeOwner(Long id, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + authentication.getName()));

        int updated = roomRepository.claimOwnership(id, user);
        if (updated == 0) throw new IllegalStateException("Someone else already became owner");
    }
}
