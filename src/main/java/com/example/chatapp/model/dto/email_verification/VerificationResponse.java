package com.example.chatapp.model.dto.email_verification;

import com.example.chatapp.model.dto.SimpleResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "VerificationResponse", description = "Response after verifying the email")
public class VerificationResponse extends SimpleResponse {

    @Schema(description = "Indicates whether the email was successfully verified", example = "true")
    private boolean verified;

    public VerificationResponse(String message, boolean success, boolean verified) {
        super(message, success);
        this.verified = verified;
    }
}
