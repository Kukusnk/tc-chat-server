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
import com.example.chatapp.model.dto.room.RoomListResponse;
import com.example.chatapp.model.dto.room.RoomSearchResponse;
import com.example.chatapp.repository.RoomRepository;
import com.example.chatapp.repository.TopicRepository;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.specification.RoomSpecifications;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    @Deprecated
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Кімната не знайдена: " + id));
    }

    @Transactional
    public boolean countByOwner(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + authentication.getName()));
        return roomRepository.countByOwner(user) < MAX_ROOMS_PER_USER;
    }

    public Page<RoomSearchResponse> searchRooms(String search,
                                                List<Long> topicIds,
                                                boolean joined,
                                                Pageable pageable,
                                                Authentication authentication)
    {
        Specification<Room> spec = buildSpecification(search, topicIds, pageable);

        if (joined) {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new UserNotFoundException("User not found: " + authentication.getName()));
            spec = spec.and(RoomSpecifications.hasMember(user.getId()));
        }

        Pageable cleanedPageable = buildCleanedPageable(pageable);

        return roomRepository.findAll(spec, cleanedPageable)
                .map(RoomSearchResponse::fromEntity);
    }

    private Specification<Room> buildSpecification(String search, List<Long> topicIds, Pageable pageable) {
        Specification<Room> spec = RoomSpecifications.hasSearchWord(search)
                .and(RoomSpecifications.hasTopics(topicIds));

        for (Sort.Order order : pageable.getSort()) {
            if (order.getProperty().equals("membersCount")) {
                spec = spec.and(RoomSpecifications.orderByMembersCount(order.getDirection()));
            }
        }

        return spec;
    }

    private Pageable buildCleanedPageable(Pageable pageable) {
        Sort sortDefault = Sort.unsorted();

        for (Sort.Order order : pageable.getSort()) {
            if (!order.getProperty().equals("membersCount")) {
                sortDefault = sortDefault.and(Sort.by(order));
            }
        }

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortDefault
        );
    }
}
