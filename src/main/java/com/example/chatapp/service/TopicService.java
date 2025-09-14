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

    private Topic getTopicById(Long id) {
        return topicRepository.findById(id)
                .orElseGet(() -> {
                    log.warn("Topic with id {} not found", id);
                    throw new TopicNotFoundException("Topic with id '" + id + "' not found");
                });
    }

    public TopicDTO createTopic(TopicDTO topicDTO) {
        log.info("Creating topic {}", topicDTO);
        Topic topic = DevTools.DTOToTopic(topicDTO);

        validateUniqueName(topic.getName(), null);

        Topic savedTopic = topicRepository.save(topic);
        return DevTools.topicsToDTO(savedTopic);
    }

    public TopicDTO updateTopic(Long id, TopicDTO topicDTO) {
        log.info("Updating topic {}, to {}", id, topicDTO);
        Topic topic = getTopicById(id);

        validateUniqueName(topicDTO.getName(), id);

        topic.setName(topicDTO.getName());
        topic.setDescription(topicDTO.getDescription());

        Topic savedTopic = topicRepository.save(topic);
        return DevTools.topicsToDTO(savedTopic);
    }

    public void deleteTopicById(Long id) {
        log.info("Deleting topic by id={}", id);
        Topic topic = getTopicById(id);

        topicRepository.delete(topic);
    }

    public TopicDTO patchTopic(Long id, TopicDTO topicDTO) {
        log.info("Patching topic {}, to {}", id, topicDTO);
        Topic topic = getTopicById(id);

        if (topicDTO.getName() != null && !topicDTO.getName().isBlank()) {
            validateUniqueName(topicDTO.getName(), id);
            topic.setName(topicDTO.getName());
        }
        if (topicDTO.getDescription() != null) {
            topic.setDescription(topicDTO.getDescription());
        }

        Topic savedTopic = topicRepository.save(topic);
        return DevTools.topicsToDTO(savedTopic);
    }
}
