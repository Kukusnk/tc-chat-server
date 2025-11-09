package com.example.chatapp.controller.rest;

import com.example.chatapp.model.User;
import com.example.chatapp.model.dto.avatar.AvatarResponse;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.service.AvatarStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
            @ApiResponse(responseCode = "200", description = "Avatar uploaded successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AvatarResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file or file validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/upload")
    public ResponseEntity<AvatarResponse> uploadAvatar(
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

            AvatarResponse response = new AvatarResponse("Avatar uploaded successfully", avatarUrl);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("Failed to upload avatar", e);
            AvatarResponse response = new AvatarResponse("Failed to upload avatar: " + e.getMessage(), "");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Unexpected error during avatar upload", e);
            AvatarResponse response = new AvatarResponse("Unexpected error: " + e.getMessage(), "");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Operation(
            summary = "Delete avatar",
            description = "Delete the current avatar of the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avatar deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AvatarResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping
    public ResponseEntity<AvatarResponse> deleteAvatar(
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

            AvatarResponse response = new AvatarResponse("Avatar deleted successfully", "");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to delete avatar", e);
            AvatarResponse response = new AvatarResponse("Failed to delete avatar: " + e.getMessage(), "");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(
            summary = "Get avatar URL",
            description = "Get the current avatar URL of the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get avatar URL",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AvatarResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping
    public ResponseEntity<AvatarResponse> getAvatar(
            @Parameter(hidden = true) Authentication authentication) {

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AvatarResponse response = new AvatarResponse("", user.getAvatarUrl());

        return ResponseEntity.ok(response);
    }
}
