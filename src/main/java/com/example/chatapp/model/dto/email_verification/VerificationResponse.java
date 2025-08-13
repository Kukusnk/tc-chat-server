package com.example.chatapp.model.dto.email_verification;

import com.example.chatapp.model.dto.ApiResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VerificationResponse extends ApiResponse {
    private boolean verified;

    public VerificationResponse(String message, boolean success, boolean verified) {
        super(message, success);
        this.verified = verified;
    }
}
