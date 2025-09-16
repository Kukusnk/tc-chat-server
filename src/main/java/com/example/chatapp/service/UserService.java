package com.example.chatapp.service;

import com.example.chatapp.handler.exception.*;
import com.example.chatapp.model.EmailVerificationCode;
import com.example.chatapp.model.User;
import com.example.chatapp.model.dto.auth.AccessToken;
import com.example.chatapp.model.dto.auth.AuthResponse;
import com.example.chatapp.model.dto.user.UserDTO;
import com.example.chatapp.repository.EmailVerificationCodeRepository;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.util.DevTools;
import com.example.chatapp.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, EmailVerificationCodeRepository emailVerificationCodeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailVerificationCodeRepository = emailVerificationCodeRepository;
    }

//    public String uploadUserAvatar(String username, MultipartFile file) throws IOException {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Validate file
//        validateAvatarFile(file);
//
//        // Upload new avatar
//        String newAvatarUrl = googleDriveService.uploadAvatar(file, username);
//        String newFileId = extractFileIdFromUrl(newAvatarUrl);
//
//        // Delete old avatar if exists
//        if (user.getAvatarFileId() != null) {
//            try {
//                googleDriveService.deleteAvatar(user.getAvatarFileId());
//            } catch (Exception e) {
//                log.warn("Failed to delete old avatar: {}", e.getMessage());
//            }
//        }
//
//        // Update user
//        user.setAvatarUrl(newAvatarUrl);
//        user.setAvatarFileId(newFileId);
//        userRepository.save(user);
//
//        return newAvatarUrl;
//    }
//
//    public void deleteUserAvatar(String username) throws IOException {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (user.getAvatarFileId() != null) {
//            googleDriveService.deleteAvatar(user.getAvatarFileId());
//            user.setAvatarUrl(null);
//            user.setAvatarFileId(null);
//            userRepository.save(user);
//        }
//    }
//
//    private void validateAvatarFile(MultipartFile file) {
//        if (file.isEmpty()) {
//            throw new RuntimeException("File is empty");
//        }
//
//        // Check file size (5MB max)
//        if (file.getSize() > 5 * 1024 * 1024) {
//            throw new RuntimeException("File size exceeds 5MB limit");
//        }
//
//        // Check file type
//        String contentType = file.getContentType();
//        if (contentType == null || !contentType.startsWith("image/")) {
//            throw new RuntimeException("Only image files are allowed");
//        }
//    }
//
//    private String extractFileIdFromUrl(String driveUrl) {
//        if (driveUrl != null && driveUrl.contains("id=")) {
//            return driveUrl.substring(driveUrl.indexOf("id=") + 3);
//        }
//        return null;
//    }


    @Transactional
    public AccessToken updateUserUsername(String username, String newUsername) {
        if (isUsernameTaken(newUsername)) {
            throw new UserUsernameException("User with username '" + newUsername + "' already exists");
        }
        userRepository.updateUsernameByUsername(username, newUsername);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));
        String token = jwtUtil.generateToken(username, user.getRoles());
        return AccessToken.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    public void updateUserPassword(String username, String newPassword) {
        userRepository.updatePasswordByUsername(username, passwordEncoder.encode(newPassword));
    }

    @Transactional
    public AuthResponse updateUserEmail(String username, String newEmail) {
        if (isEmailExist(newEmail)) {
            throw new UserEmailException("User with email '" + newEmail + "' already exists");
        }

        if (!isEmailVerified(newEmail)) {
            throw new UnverifiedEmailException("Email verification failed: " + newEmail + " is not verified");
        }

        userRepository.updateEmailByUsername(username, newEmail);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));
        String accessToken = jwtUtil.generateToken(username, user.getRoles());
        String refreshToken = jwtUtil.generateResetToken(newEmail);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    public boolean isEmailVerified(String email) {
        return emailVerificationCodeRepository
                .findByEmailAndCodeTypeAndIsUsed(email, EmailVerificationCode.CodeType.EMAIL_VERIFICATION, true)
                .isPresent();
    }

    @Transactional
    public void deleteUserByUsername(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials, password does not match");
        }
        userRepository.delete(user);
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

    public UserDTO getUserDTOByUsernameOrThrow(String username) {
        return DevTools.userToDTO(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found")));
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

    public AuthResponse updateUser(UserDTO userData, String username) {
        User user = getUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));
        String newUsername = userData.getUsername();
        String accessToken = null;
        if (!user.getUsername().equals(newUsername)) {
            if (isUsernameTaken(newUsername)) {
                throw new UserUsernameException("User with username '" + newUsername + "' already exists");
            }
            user.setUsername(newUsername);
            accessToken = jwtUtil.generateToken(newUsername, user.getRoles());
        }
        if (!passwordEncoder.matches(userData.getPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(userData.getPassword()));
        }

        String newEmail = userData.getEmail();
        String refreshToken = "";
        if (!user.getEmail().equals(newEmail)) {
            if (isEmailExist(newEmail)) {
                throw new UserEmailException("User with email '" + newEmail + "' already exists");
            }
            if (!isEmailVerified(newEmail)) {
                throw new UnverifiedEmailException("Email verification failed: " + newEmail + " is not verified");
            }
            user.setEmail(newEmail);
            refreshToken = jwtUtil.generateResetToken(newEmail);
            if (accessToken == null) {
                accessToken = jwtUtil.generateToken(username, user.getRoles());
            }
        }

        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }
}
