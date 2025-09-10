package com.example.chatapp.controller.rest;

import com.example.chatapp.model.dto.topic.TopicDTO;
import com.example.chatapp.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Tag(name = "Topics", description = "API for managing chat topics")
public class TopicController {
    private final TopicService topicService;

    @GetMapping
    @Operation(summary = "Get all topics")
    @ApiResponse(responseCode = "200", description = "Successfully returned topics list")
    public ResponseEntity<List<TopicDTO>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @GetMapping("/search")
    @Operation(summary = "Get all topics by name")
    @ApiResponse(responseCode = "200", description = "Successfully returned topics list by name")
    public ResponseEntity<List<TopicDTO>> getAllTopicsByName(@RequestParam String name) {
        return ResponseEntity.ok(topicService.getTopicsByName(name));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get topic by id")
    @ApiResponse(responseCode = "200", description = "Successfully returned topic by id")
    public ResponseEntity<TopicDTO> getTopicById(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.getTopicById(id));
    }
}
