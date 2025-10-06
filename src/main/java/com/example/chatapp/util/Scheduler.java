package com.example.chatapp.util;

import com.example.chatapp.model.Room;
import com.example.chatapp.repository.MessageRepository;
import com.example.chatapp.repository.RoomRepository;
import com.example.chatapp.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private final EmailVerificationService emailVerificationService;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;

    @Scheduled(fixedRate = 3600000) // 1 hour = 3600000 ms
    public void cleanupExpiredCodes() {
        emailVerificationService.cleanupExpiredCodes();
        log.info("Cleanup expired codes completed");
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteOrphanedRooms() {
        LocalDateTime now = LocalDateTime.now();
        List<Room> expired = roomRepository.findAllByDeleteAfterBefore(now);

        if (expired.isEmpty()) {
            log.info("No orphaned rooms to delete");
            return;
        }

        List<Long> roomIds = expired.stream().map(Room::getId).toList();
        messageRepository.deleteByRoomIdIn(roomIds);
        log.info("Deleted messages from {} orphaned rooms", roomIds.size());

        roomRepository.deleteAll(expired);
        log.info("Deleted {} orphaned rooms after {}", expired.size(), now);
    }
}
