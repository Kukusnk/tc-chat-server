package com.example.chatapp.controller.rest;

import com.example.chatapp.model.dto.topic.TopicDTO;
import com.example.chatapp.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/topics")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin Topic API", description = "API for altering chat topics")
public class AdminTopicController {
    private final TopicService topicService;

    @PostMapping
    @Operation(
            summary = "Add new topic",
            description = """
                Creates a new topic in the chat system.
                
                Possible error responses:
                - 400: Validation errors
                - 403: Forbidden
                - 405: Method not supported
                - 409: Conflict
                - 500: Internal server error
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Topic added successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TopicDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Validation error", value = "Name must not be blank"),
                            })),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(mediaType = "application/json",
                        examples = {
                                @ExampleObject(name = "Conflict Error", value = "Topic named 'Java' already exists")
                        })),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<TopicDTO> createTopic(@RequestBody @Valid TopicDTO topicDTO) {
        TopicDTO topic = topicService.createTopic(topicDTO);
        return new ResponseEntity<>(topic, HttpStatus.CREATED);
    }


    @PutMapping
    @Operation(
            summary = "Topic update",
            description = """
                Updates an existing topic.
                
                Possible error responses:
                - 400: Validation errors
                - 403: Forbidden
                - 404: Topic not found
                - 405: Method not supported
                - 409: Conflict
                - 500: Internal server error
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Topic updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TopicDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Validation error", value = "Name must not be blank"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Topic not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Not found", value = "Topic with name 'Java' not found"))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Conflict Error", value = "Topic named 'Java' already exists"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<TopicDTO> updateTopic(@RequestParam String name,
                                                @RequestBody @Valid TopicDTO topicDTO) {
        TopicDTO topic = topicService.updateTopic(name, topicDTO);
        return ResponseEntity.ok(topic);
    }

    @DeleteMapping
    @Operation(
            summary = "Delete topic",
            description = """
                Deletes an existing topic by its name.
                
                Possible error responses:
                - 403: Forbidden
                - 404: Topic not found
                - 500: Internal server error
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Topic deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Topic not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Not found", value = "Topic with name 'Java' not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Void> deleteTopic(@RequestParam String name) {
        topicService.deleteTopic(name);
        return ResponseEntity.noContent().build();
    }
}
