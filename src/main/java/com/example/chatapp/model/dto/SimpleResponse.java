package com.example.chatapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "SimpleResponse", description = "Basic response containing message and status")
public class SimpleResponse {

    @Schema(description = "Response message", example = "Email successfully confirmed")
    private String message;

    @Schema(description = "Whether the operation was successful", example = "true")
    private boolean success;
}
