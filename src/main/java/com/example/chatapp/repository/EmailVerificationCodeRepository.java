package com.example.chatapp.repository;

import com.example.chatapp.model.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {

    // Find the active code for email
    Optional<EmailVerificationCode> findByEmailAndCodeAndIsUsedFalseAndExpiresAtAfter(
            String email, String code, LocalDateTime now);

    // Find recently sent code (spam protection)
    Optional<EmailVerificationCode> findByEmailAndCreatedAtAfter(
            String email, LocalDateTime after);

    // Mark all email codes as used
    @Modifying
    @Query("UPDATE EmailVerificationCode e SET e.isUsed = true WHERE e.email = :email AND e.isUsed = false")
    void markAllAsUsedByEmail(@Param("email") String email);

    // Delete expired codes
    @Modifying
    @Query("DELETE FROM EmailVerificationCode e WHERE e.expiresAt < :now")
    void deleteExpiredCodes(@Param("now") LocalDateTime now);

    // Statistics
    long countByEmail(String email);

    long countByEmailAndIsUsedTrue(String email);

    long countByExpiresAtBefore(LocalDateTime now);
}