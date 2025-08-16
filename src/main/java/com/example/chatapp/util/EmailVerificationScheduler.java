package com.example.chatapp.util;

import com.example.chatapp.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationScheduler {

    private final EmailVerificationService emailVerificationService;

    @Scheduled(fixedRate = 3600000) // 1 hour = 3600000 ms
    public void cleanupExpiredCodes() {
        emailVerificationService.cleanupExpiredCodes();
        log.info("Cleanup expired codes completed");
    }
}
