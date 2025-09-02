package com.example.chatapp.service;

import com.example.chatapp.handler.exception.UserNotFoundException;
import com.example.chatapp.handler.exception.VerificationException;
import com.example.chatapp.model.User;
import com.example.chatapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleDriveService googleDriveService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, GoogleDriveService googleDriveService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.googleDriveService = googleDriveService;
    }

    public String uploadUserAvatar(String username, MultipartFile file) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate file
        validateAvatarFile(file);

        // Upload new avatar
        String newAvatarUrl = googleDriveService.uploadAvatar(file, username);
        String newFileId = extractFileIdFromUrl(newAvatarUrl);

        // Delete old avatar if exists
        if (user.getAvatarFileId() != null) {
            try {
                googleDriveService.deleteAvatar(user.getAvatarFileId());
            } catch (Exception e) {
                log.warn("Failed to delete old avatar: {}", e.getMessage());
            }
        }

        // Update user
        user.setAvatarUrl(newAvatarUrl);
        user.setAvatarFileId(newFileId);
        userRepository.save(user);

        return newAvatarUrl;
    }

    public void deleteUserAvatar(String username) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getAvatarFileId() != null) {
            googleDriveService.deleteAvatar(user.getAvatarFileId());
            user.setAvatarUrl(null);
            user.setAvatarFileId(null);
            userRepository.save(user);
        }
    }

    private void validateAvatarFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // Check file size (5MB max)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size exceeds 5MB limit");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }
    }

    private String extractFileIdFromUrl(String driveUrl) {
        if (driveUrl != null && driveUrl.contains("id=")) {
            return driveUrl.substring(driveUrl.indexOf("id=") + 3);
        }
        return null;
    }


    @Transactional
    public void verifiedUserByEmail(String email) {
        User user = getUserByEmailOrThrow(email);
        if (!user.getIsEmailVerified()) {
            user.setIsEmailVerified(true);
        } else {
            log.info("User already verified");
            throw new VerificationException("Email " + email + " already verified");
        }
        userRepository.save(user);
        log.info("User verified");
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email '" + email + "' not found"));
    }

    public User getUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));
    }

    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public void updatePasswordByEmail(String email, String encodedPassword) {
        userRepository.updatePasswordByEmail(email, encodedPassword);
    }
}
