package com.example.chatapp.controller.rest;

import com.example.chatapp.model.dto.auth.AccessToken;
import com.example.chatapp.model.dto.auth.AuthResponse;
import com.example.chatapp.model.dto.user.*;
import com.example.chatapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "Operations for managing the current user's account")
public class UserController {

    private final UserService userService;

//    @PostMapping("/{username}/avatar")
//    public ResponseEntity<?> uploadAvatar(
//            @PathVariable String username,
//            @RequestParam("file") MultipartFile file) {
//        try {
//            String avatarUrl = userService.uploadUserAvatar(username, file);
//            return ResponseEntity.ok(Map.of(
//                    "message", "Avatar uploaded successfully",
//                    "avatarUrl", avatarUrl
//            ));
//        } catch (Exception e) {
//            log.error("Error uploading avatar: {}", e.getMessage());
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", e.getMessage()));
//        }
//    }

//    @DeleteMapping("/{username}/avatar")
//    public ResponseEntity<?> deleteAvatar(@PathVariable String username) {
//        try {
//            userService.deleteUserAvatar(username);
//            return ResponseEntity.ok(Map.of("message", "Avatar deleted successfully"));
//        } catch (Exception e) {
//            log.error("Error deleting avatar: {}", e.getMessage());
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", e.getMessage()));
//        }
//    }

    /**
     * Get current user's profile information
     */
    @Operation(
            summary = "Get user profile",
            description = """
                    Returns profile information of the currently authenticated user.
                    
                    Possible error responses:
                    - 400: User not found
                    - 401: Unauthorized (no or invalid token)
                    - 405: Method not supported
                    - 500: Internal server error
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "User with username 'BohdanTaran' not found"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "Full authentication is required to access this resource"))),
            @ApiResponse(responseCode = "405", description = "Method not allowed",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "The method is not supported: POST"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "Internal error: NullPointerException")))
    })
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUserProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserDTOByUsernameOrThrow(authentication.getName()));
    }

    /**
     * Update current user's password
     */
    @Operation(
            summary = "Update password",
            description = """
                    Updates the password of the current user.
                    
                    Possible error responses:
                    - 400: Validation errors (weak password, blank password, wrong old password)
                    - 401: Unauthorized
                    - 405: Method not supported
                    - 500: Internal server error
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated",
                    content = @Content(mediaType = "text/plain",
                            examples = @ExampleObject(value = "Password updated"))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "Password must be at least 8 characters long"),
                                    @ExampleObject(name = "Wrong old password", value = "Current password is incorrect")
                            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "Full authentication is required to access this resource"))),
            @ApiResponse(responseCode = "405", description = "Method not allowed",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "The method is not supported: POST"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "Internal error: NullPointerException")))
    })
    @PutMapping("/me/password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdateUserPasswordDTO password, Authentication authentication) {
        userService.updateUserPassword(authentication.getName(), password.password());
        return ResponseEntity.ok("Password updated");
    }


    /**
     * Update current user's username
     */
    @Operation(
            summary = "Update username",
            description = """
                    Updates the username of the current user.
                    
                    Possible error responses:
                    - 400: Validation errors (blank username, too short/long), User not found
                    - 401: Unauthorized
                    - 409: Conflict (username already exists)
                    - 405: Method not supported
                    - 500: Internal server error
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Username updated, new access token returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccessToken.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation errors", value = "Username must be between 4 and 16"),
                                    @ExampleObject(name = "User not found", value = "User with username 'BohdanTaran' not found")
                            })),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "User with username 'BohdanTaran' already exists")))
    })
    @PutMapping("/me/username")
    public ResponseEntity<AccessToken> updateUsername(@RequestBody UpdateUserUsernameDTO username, Authentication authentication) {
        AccessToken token = userService.updateUserUsername(authentication.getName(), username.username());
        return ResponseEntity.ok(token);
    }

    /**
     * Update current user's email
     */
    @Operation(
            summary = "Update email",
            description = """
                    Updates the email address of the current user and returns new authentication tokens.
                    
                    Possible error responses:
                    - 400: Validation errors (invalid format, blank email), User not found
                    - 401: Unauthorized
                    - 409: Conflict (email already exists)
                    - 405: Method not supported
                    - 500: Internal server error
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email updated, new tokens returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation errors", value = "Email should be valid"),
                                    @ExampleObject(name = "User not found", value = "User with username 'BohdanTaran' not found")
                            })),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "User with email 'john@example.com' already exists")))
    })
    @PutMapping("/me/email")
    public ResponseEntity<AuthResponse> updateEmail(@RequestBody UpdateUserEmailDTO email, Authentication authentication) {
        AuthResponse token = userService.updateUserEmail(authentication.getName(), email.email());
        return ResponseEntity.ok(token);
    }

    /**
     * Delete current user's account
     */
    @Operation(
            summary = "Delete user",
            description = """
                    Deletes the current user's account.
                    
                    Possible error responses:
                    - 400: Bad request (wrong password), User not found
                    - 401: Unauthorized
                    - 405: Method not supported
                    - 500: Internal server error
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted",
                    content = @Content(mediaType = "text/plain",
                            examples = @ExampleObject(value = "User deleted"))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = "Wrong password"),
                                    @ExampleObject(name = "User not found", value = "User with username 'BohdanTaran' not found")
                            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "Full authentication is required to access this resource"))),
    })
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteUser(@RequestBody DeleteRequest password, Authentication authentication) {
        userService.deleteUserByUsername(authentication.getName(), password.password());
        return ResponseEntity.ok("User deleted");
    }
}
