package com.example.chatapp.controller.rest;

import com.example.chatapp.model.dto.topic.TopicDTO;
import com.example.chatapp.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Tag(name = "Topic API", description = "API for managing chat topics")
public class TopicController {
    private final TopicService topicService;

    @GetMapping
    @Operation(
            summary = "Get all topics",
            description = """
                Returns a list of all chat topics.
                
                Possible error responses:
                - 405: Method not supported
                - 500: Internal server error
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returned topics list by name"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<TopicDTO>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @GetMapping("/search")
    @Operation(
            summary = "Get topics by name",
            description = """
                Returns a list of topics filtered by name (case-insensitive, partial match).
                
                Possible error responses:
                - 405: Method not supported
                - 500: Internal server error
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returned topics list by name"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<TopicDTO>> getAllTopicsByName(@RequestParam String name) {
        return ResponseEntity.ok(topicService.getTopicsByName(name));
    }
}
