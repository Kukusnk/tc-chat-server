package com.example.chatapp.model.dto.topic;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "CreateTopicDTO", description = "DTO for creating a new topic")
public class CreateTopicDTO {

    @NotBlank(message = "Name must not be blank")
    private String name;
}