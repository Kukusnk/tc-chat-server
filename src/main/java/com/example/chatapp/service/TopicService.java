package com.example.chatapp.service;

import com.example.chatapp.handler.exception.TopicNotFoundException;
import com.example.chatapp.handler.exception.TopicUniqueException;
import com.example.chatapp.model.Topic;
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

    private void validateUniqueName(String name, Long excludeId) {
        topicRepository.findByName(name)
                .filter(topic -> !topic.getId().equals(excludeId))
                .ifPresent(t -> {
                    log.warn("Topic already exists: {}", name);
                    throw new TopicUniqueException("Topic named '" + name + "' already exists");
                });
    }

    private Topic getTopicByName(String name) {
        return topicRepository.findByName(name)
                .orElseGet(() -> {
                    log.warn("Topic with name {} not found", name);
                    throw new TopicNotFoundException("Topic with name '" + name + "' not found");
                });
    }

    public TopicDTO createTopic(TopicDTO topicDTO) {
        log.info("Creating topic {}", topicDTO);
        Topic topic = DevTools.DTOToTopic(topicDTO);

        validateUniqueName(topic.getName(), null);

        Topic savedTopic = topicRepository.save(topic);
        return DevTools.topicsToDTO(savedTopic);
    }

    public TopicDTO updateTopic(String name, TopicDTO topicDTO) {
        log.info("Updating topic {}, to {}", name, topicDTO);
        Topic topic = getTopicByName(name);

        validateUniqueName(topicDTO.getName(), topic.getId());

        topic.setName(topicDTO.getName());

        Topic savedTopic = topicRepository.save(topic);
        return DevTools.topicsToDTO(savedTopic);
    }

    public void deleteTopic(String name) {
        log.info("Deleting topic by name={}", name);
        Topic topic = getTopicByName(name);

        topicRepository.delete(topic);
    }
}
