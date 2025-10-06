package com.example.chatapp.util;

import com.example.chatapp.model.Message;
import com.example.chatapp.model.Topic;
import com.example.chatapp.model.User;
import com.example.chatapp.model.dto.message.MessageDTO;
import com.example.chatapp.model.dto.topic.CreateTopicDTO;
import com.example.chatapp.model.dto.topic.TopicDTO;
import com.example.chatapp.model.dto.user.UserDTO;

public class DevTools {

    public static MessageDTO messageToDTO(Message message) {
        return new MessageDTO(
                message.getSender(),
                message.getContent(),
                message.getTimestamp()
        );
    }

    public static UserDTO userToDTO(User user) {
        return UserDTO.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .password(user.getPassword())
                .isEmailVerified(user.getIsEmailVerified())
                .build();
    }

    public static TopicDTO topicsToDTO(Topic topic) {
        return TopicDTO.builder()
                .id(topic.getId())
                .name(topic.getName())
                .build();
    }

    public static Topic DTOToTopic(CreateTopicDTO topicDTO) {
        return Topic.builder()
                .name(topicDTO.getName())
                .build();
    }
}
