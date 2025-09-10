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
@Schema(name = "TopicDTO", description = "Topic Dto")
public class TopicDTO {
    @NotBlank
    private String name;

    private String description;
}
