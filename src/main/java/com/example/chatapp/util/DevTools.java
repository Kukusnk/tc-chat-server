package com.example.chatapp.util;

import com.example.chatapp.model.Message;
import com.example.chatapp.model.Room;
import com.example.chatapp.model.Topic;
import com.example.chatapp.model.User;
import com.example.chatapp.model.dto.message.MessageDTO;
import com.example.chatapp.model.dto.room.RoomDTO;
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

    public static RoomDTO roomsToDTO(Room room) {
        return new RoomDTO(room.getName());
    }

    public static TopicDTO topicsToDTO(Topic topic) {
        return TopicDTO.builder()
                .name(topic.getName())
                .build();
    }

    public static Topic DTOToTopic(TopicDTO topicDTO) {
        return Topic.builder()
                .name(topicDTO.getName())
                .build();
    }
}
