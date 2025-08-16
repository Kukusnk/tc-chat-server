package com.example.chatapp.controller.rest;

import com.example.chatapp.model.dto.SimpleResponse;
import com.example.chatapp.model.dto.auth.AuthResponse;
import com.example.chatapp.model.dto.auth.LoginRequest;
import com.example.chatapp.model.dto.auth.RegisterRequest;
import com.example.chatapp.model.dto.email_verification.SendVerificationCodeRequest;
import com.example.chatapp.model.dto.email_verification.VerificationResponse;
import com.example.chatapp.model.dto.email_verification.VerifyEmailRequest;
import com.example.chatapp.model.dto.refresh_token.RefreshTokenRequest;
import com.example.chatapp.service.AuthService;
import com.example.chatapp.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Email Verification", description = "API for authentication and email verification")
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @Operation(
            summary = "Register user",
            description = """
                    Creates a new user account and returns JWT authentication tokens.
                    
                    Possible error responses:
                    - 400: Validation errors, username/email already exists, verification errors, wrong arguments
                    - 405: Method not supported
                    - 500: Internal server error
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully registered",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Username exists", value = "User with username 'john_doe' already exists"),
                                    @ExampleObject(name = "Email exists", value = "User with email 'john@example.com' already exists"),
                                    @ExampleObject(name = "Validation errors", value = "Username must be between 4 and 16, Password must be greater than or equal to 8"),
                                    @ExampleObject(name = "Wrong arguments", value = "Wrong argument: invalid data format")
                            })),
            @ApiResponse(responseCode = "405", description = "Method not allowed",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Method not supported",
                                    value = "The method is not supported: PUT"
                            ))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Unexpected error",
                                    value = "Internal error: NullPointerException"
                            )))
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Registration data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            )
            @Valid @RequestBody RegisterRequest request) {
        log.info("Registering new user: {}", request);
        AuthResponse response = authService.register(request);
        log.info("Registered new user: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Log in existing user and return tokens
     */
    @Operation(
            summary = "Login user",
            description = """
                    Authenticates the user and returns JWT authentication tokens.
                    
                    Possible error responses:
                    - 400: Validation errors (missing fields, wrong format)
                    - 401: Invalid credentials (username/email or password is incorrect)
                    - 405: Method not supported
                    - 500: Internal server error
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Validation errors",
                                    value = "Username or email must not be blank, password must not be blank"
                            ))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Email not found", value = "Invalid credentials, email does not exist"),
                                    @ExampleObject(name = "Username not found", value = "Invalid credentials, username does not exist"),
                                    @ExampleObject(name = "Wrong password", value = "Invalid credentials, password does not match")
                            })),
            @ApiResponse(responseCode = "405", description = "Method not allowed",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Method not supported",
                                    value = "The method is not supported: GET"
                            ))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Unexpected error",
                                    value = "Internal error: NullPointerException"
                            )))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh authentication token using refresh token
     */
    @Operation(
            summary = "Refresh token",
            description = "Generates a new access token using a valid refresh token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token successfully refreshed",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Token refreshed",
                                    value = "{ \"accessToken\": \"new-jwt-access-token\", \"refreshToken\": \"new-jwt-refresh-token\" }"
                            ))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh token payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RefreshTokenRequest.class),
                            examples = @ExampleObject(
                                    name = "Sample refresh request",
                                    value = "{ \"refreshToken\": \"jwt-refresh-token\" }"
                            )
                    )
            )
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout current authenticated user
     */
    @Operation(
            summary = "Logout user",
            description = "Invalidates the refresh token and ends the user session"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful",
                    content = @Content(mediaType = "text/plain",
                            examples = @ExampleObject(value = "Logged out successfully")))
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @Parameter(hidden = true) Authentication authentication) {
        authService.logout(authentication.getName());
        return ResponseEntity.ok("Logged out successfully");
    }

    /**
     * Send an email verification code to the user
     */
    @Operation(
            summary = "Send email verification code",
            description = "Sends a verification code to the provided email address"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verification code sent",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Verification code sent",
                                    value = "{ \"message\": \"Verification code sent to your email\", \"success\": true }"
                            ))),
            @ApiResponse(responseCode = "400", description = "Email already verified or invalid")
    })
    @PostMapping("/email-verification/send-code")
    public ResponseEntity<SimpleResponse> sendCode(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email address for verification",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SendVerificationCodeRequest.class),
                            examples = @ExampleObject(
                                    name = "Sample email request",
                                    value = "{ \"email\": \"john@example.com\" }"
                            )
                    )
            )
            @Valid @RequestBody SendVerificationCodeRequest request) {
        emailVerificationService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok(new SimpleResponse("Verification code sent to your email", true));
    }

    /**
     * Verify the email using the provided code
     */
    @Operation(
            summary = "Verify email",
            description = "Validates the verification code for the given email"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email successfully verified",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Email verified",
                                    value = "{ \"message\": \"Email successfully confirmed\", \"success\": true, \"verified\": true }"
                            ))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired verification code")
    })
    @PostMapping("/email-verification/verify")
    public ResponseEntity<VerificationResponse> verifyCode(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email and verification code",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = VerifyEmailRequest.class),
                            examples = @ExampleObject(
                                    name = "Sample verification",
                                    value = "{ \"email\": \"john@example.com\", \"code\": \"123456\" }"
                            )
                    )
            )
            @Valid @RequestBody VerifyEmailRequest request) {
        boolean verified = emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(new VerificationResponse(
                "Email successfully confirmed", true, verified));
    }

    /**
     * Get statistics about email verifications
     */
    @Operation(
            summary = "Get email verification stats",
            description = "Returns verification statistics. If email is provided, returns stats only for that email"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Statistics example",
                                    value = "{ \"totalSent\": 150, \"totalVerified\": 120, \"verificationRate\": 0.8 }"
                            )))
    })
    @GetMapping("/email-verification/stats")
    public ResponseEntity<?> getStats(
            @Parameter(description = "Optional email filter", required = false,
                    example = "john@example.com")
            @RequestParam(required = false) String email) {
        return ResponseEntity.ok(emailVerificationService.getVerificationStats(email));
    }
}
