package com.example.chatapp.controller.rest;

import com.example.chatapp.model.User;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.service.AvatarStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/avatar")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Avatar API", description = "API for managing user avatars")
public class AvatarController {

    private final AvatarStorageService avatarService;
    private final UserRepository userRepository;

    @Operation(
            summary = "Upload avatar",
            description = "Upload a new avatar image for the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avatar uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or file validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @Parameter(description = "Avatar image file", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(hidden = true) Authentication authentication) {

        try {
            String username = authentication.getName();
            log.info("Uploading avatar for user: {}", username);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Delete the old avatar if you have one
            if (user.getAvatarUrl() != null) {
                avatarService.deleteAvatar(user.getAvatarUrl());
            }

            // Uploading a new avatar
            String avatarUrl = avatarService.uploadAvatar(file, username);

            // Updating the user in the database
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Avatar uploaded successfully");
            response.put("avatarUrl", avatarUrl);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("Failed to upload avatar", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload avatar: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Unexpected error during avatar upload", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unexpected error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @Operation(
            summary = "Delete avatar",
            description = "Delete the current avatar of the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avatar deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteAvatar(
            @Parameter(hidden = true) Authentication authentication) {

        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getAvatarUrl() != null) {
                avatarService.deleteAvatar(user.getAvatarUrl());
                user.setAvatarUrl(null);
                userRepository.save(user);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Avatar deleted successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to delete avatar", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete avatar: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(
            summary = "Get avatar URL",
            description = "Get the current avatar URL of the authenticated user"
    )
    @GetMapping
    public ResponseEntity<Map<String, String>> getAvatar(
            @Parameter(hidden = true) Authentication authentication) {

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, String> response = new HashMap<>();
        response.put("avatarUrl", user.getAvatarUrl());

        return ResponseEntity.ok(response);
    }
}
