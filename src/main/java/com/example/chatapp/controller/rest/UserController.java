package com.example.chatapp.controller.rest;

import com.example.chatapp.model.User;
import com.example.chatapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/{username}/avatar")
    public ResponseEntity<?> uploadAvatar(
            @PathVariable String username,
            @RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = userService.uploadUserAvatar(username, file);
            return ResponseEntity.ok(Map.of(
                    "message", "Avatar uploaded successfully",
                    "avatarUrl", avatarUrl
            ));
        } catch (Exception e) {
            log.error("Error uploading avatar: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{username}/avatar")
    public ResponseEntity<?> deleteAvatar(@PathVariable String username) {
        try {
            userService.deleteUserAvatar(username);
            return ResponseEntity.ok(Map.of("message", "Avatar deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting avatar: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserProfile(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
