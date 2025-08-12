package com.example.chatapp.util;

import com.example.chatapp.model.Message;
import com.example.chatapp.model.Room;
import com.example.chatapp.model.User;
import com.example.chatapp.model.dto.MessageDTO;
import com.example.chatapp.model.dto.RoomDTO;
import com.example.chatapp.model.dto.UserDTO;

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
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isEmailVerified(user.getIsEmailVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static RoomDTO roomsToDTO(Room room) {
        return new RoomDTO(room.getName());
    }
}
