package com.example.chatapp.service;

import com.example.chatapp.handler.exception.TopicNotFoundException;
import com.example.chatapp.model.dto.topic.TopicDTO;
import com.example.chatapp.repository.TopicRepository;
import com.example.chatapp.util.DevTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {
    private final TopicRepository topicRepository;

    public List<TopicDTO> getAllTopics() {
        log.info("Fetching all topics");
        return topicRepository.findAll().stream()
                .map(DevTools::topicsToDTO)
                .toList();
    }

    public List<TopicDTO> getTopicsByName(String name) {
        log.info("Fetching topic by name={}", name);
        return topicRepository.findByNameContainingIgnoreCase(name).stream()
                .map(DevTools::topicsToDTO)
                .toList();
    }

    public TopicDTO getTopicById(Long id) {
        log.info("Fetching topic by id={}", id);
        return topicRepository.findById(id)
                .map(DevTools::topicsToDTO)
                .orElseThrow(() -> {
                    log.warn("Topic not found: id={}", id);
                    return new TopicNotFoundException("Topic not found: " + id);
                });
    }
}
