package com.example.chatapp.service;


import com.example.chatapp.handler.exception.BadRequestException;
import com.example.chatapp.model.EmailVerificationCode;
import com.example.chatapp.repository.EmailVerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmailVerificationService {
    private final EmailVerificationCodeRepository repository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final UserService userService;

    private String generateCode() {
        int code = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(code);
    }

    public void sendVerificationCode(String email) {
        // Check spam protection (no more than one code per minute)
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);

        repository.findByEmailAndCreatedAtAfter(email, oneMinuteAgo)
                .ifPresent(recentCode -> {
                    throw new BadRequestException(
                            "The code has already been sent. Wait a minute before resending.");
                });

        // Deactivate all previous codes
        repository.markAllAsUsedByEmail(email);

        // Create a new code
        String code = generateCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        EmailVerificationCode verificationCode = EmailVerificationCode.builder()
                .email(email)
                .code(code)
                .expiresAt(expiresAt)
                .isUsed(false)
                .build();

        repository.save(verificationCode);

        // Send email
        try {
            emailService.sendVerificationEmail(email, code);
            log.info("The verification code is designed to {}", email);
        } catch (Exception e) {
            log.error("Error sending code for {}: {}", email, e.getMessage());
            throw new BadRequestException("Failed to send verification code");
        }
    }

    @Transactional
    public boolean verifyCode(String email, String code) {
        LocalDateTime now = LocalDateTime.now();

        EmailVerificationCode verificationCode = repository
                .findByEmailAndCodeAndIsUsedFalseAndExpiresAtAfter(email, code, now)
                .orElseThrow(() -> new BadRequestException("Incorrect or expired verification code"));

        // Mark it as used
        verificationCode.setIsUsed(true);
        repository.save(verificationCode);

        log.info("The code has been successfully verified for {}", email);
        userService.verifiedUserByEmail(email);
        return true;
    }

    public void cleanupExpiredCodes() {
        repository.deleteExpiredCodes(LocalDateTime.now());
        log.info("Obsolete verification codes have been removed");
    }

    // Statistics
    public Map<String, Long> getVerificationStats(String email) {
        Map<String, Long> stats = new HashMap<>();

        if (email != null) {
            stats.put("total", repository.countByEmail(email));
            stats.put("used", repository.countByEmailAndIsUsedTrue(email));
        } else {
            stats.put("total", repository.count());
            stats.put("expired", repository.countByExpiresAtBefore(LocalDateTime.now()));
        }

        return stats;
    }
}
