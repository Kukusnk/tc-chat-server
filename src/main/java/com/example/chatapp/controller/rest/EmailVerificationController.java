package com.example.chatapp.controller.rest;

import com.example.chatapp.model.dto.SimpleResponse;
import com.example.chatapp.model.dto.email_verification.SendVerificationCodeRequest;
import com.example.chatapp.model.dto.email_verification.VerificationResponse;
import com.example.chatapp.model.dto.email_verification.VerifyEmailRequest;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Email Verification API", description = "API for email verification")
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

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
            @ApiResponse(responseCode = "400", description = "Invalid or expired verification code",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Invalid email code",
                                    value = "{ \"message\": \"Incorrect or expired verification code\", \"success\": false, \"verified\": false }"
                            )))
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
