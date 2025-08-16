package com.example.chatapp.service;


import com.example.chatapp.handler.exception.BadRequestException;
import com.example.chatapp.model.EmailVerificationCode;
import com.example.chatapp.model.User;
import com.example.chatapp.repository.EmailVerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResetPasswordService {

    private final EmailVerificationCodeRepository codeRepository;
    private final EmailService emailService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    private String generateCode() {
        int code = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * Step 1: Send password reset code
     */
    public void sendPasswordResetCode(String email) {
        // Check if there is a user with this email address
        if (!userService.isEmailExist(email)) {
            // For security reasons, we do not disclose that the email was not found
            log.warn("Request to reset password for a non-existent email address: {}", email);
            return;
        }

        // Spam protection - no more than one code per minute
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        codeRepository.findByEmailAndCodeTypeAndCreatedAtAfter(email, EmailVerificationCode.CodeType.PASSWORD_RESET, oneMinuteAgo)
                .ifPresent(recentCode -> {
                    throw new BadRequestException(
                            "The code has already been sent. Wait a moment before resending.");
                });

        // Deactivate all previous password reset codes
        codeRepository.markAllAsUsedByEmailAndType(email, EmailVerificationCode.CodeType.PASSWORD_RESET);

        // Create a new code
        String code = generateCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        EmailVerificationCode resetCode = EmailVerificationCode.builder()
                .email(email)
                .code(code)
                .codeType(EmailVerificationCode.CodeType.PASSWORD_RESET)
                .expiresAt(expiresAt)
                .isUsed(false)
                .build();

        codeRepository.save(resetCode);

        // Send email
        try {
            emailService.sendPasswordResetEmail(email, code);
            log.info("The password reset code is designed to {}", email);
        } catch (Exception e) {
            log.error("Error sending reset code for {}: {}", email, e.getMessage());
            throw new BadRequestException("Failed to send password reset code");
        }
    }

    /**
     * Step 2: Check the password reset code
     */
    public boolean verifyResetCode(String email, String code) {
        LocalDateTime now = LocalDateTime.now();

        EmailVerificationCode resetCode = codeRepository
                .findByEmailAndCodeAndCodeTypeAndIsUsedFalseAndExpiresAtAfter(
                        email, code, EmailVerificationCode.CodeType.PASSWORD_RESET, now)
                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset code"));

        log.info("Password reset code successfully verified for{}", email);
        return true;
    }

    /**
     * Step 3: Reset password with code verification
     */
//    public void resetPassword(String email, String code, String newPassword) {
//        LocalDateTime now = LocalDateTime.now();
//
//        EmailVerificationCode resetCode = codeRepository
//                .findByEmailAndCodeAndCodeTypeAndIsUsedFalseAndExpiresAtAfter(
//                        email, code, EmailVerificationCode.CodeType.PASSWORD_RESET, now)
//                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset code"));
//
//        if (!userService.isEmailExist(email)) {
//            throw new UserNotFoundException("User not found");
//        }
//
//        try {
//            User user = userService.getUserByEmailOrThrow(email);
//
//            String encodedPassword = passwordEncoder.encode(newPassword);
//            userService.updatePasswordByEmail(email, encodedPassword);
//
//            resetCode.setIsUsed(true);
//            codeRepository.save(resetCode);
//
//            // IMPORTANT: Invalidate all active user tokens
//            refreshTokenService.deleteAllByUser(user);
//
//            log.info("Password successfully reset for the user {}", email);
//
//        } catch (Exception e) {
//            log.error("Password reset error for {}: {}", email, e.getMessage());
//            throw new BadRequestException("Failed to reset the password");
//        }
//    }
    public void resetPassword(String email, String newPassword) {
        try {
            String encodedPassword = passwordEncoder.encode(newPassword);
            userService.updatePasswordByEmail(email, encodedPassword);

            User user = userService.getUserByEmailOrThrow(email);
            refreshTokenService.deleteAllByUser(user);

            log.info("Password successfully reset for the user {}", email);

        } catch (Exception e) {
            log.error("Password reset error for {}: {}", email, e.getMessage());
            throw new BadRequestException("Failed to reset the password");
        }
    }

    /**
     * Combined method - code verification and password reset in one step
     * Alternative for a simpler floo
     */
    public void verifyAndResetPassword(String email, String newPassword) {
        resetPassword(email, newPassword);
    }
}
