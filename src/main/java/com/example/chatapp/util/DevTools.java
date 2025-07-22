package com.example.chatapp.util;

import com.example.chatapp.model.Message;
import com.example.chatapp.model.Room;
import com.example.chatapp.model.dto.MessageDTO;
import com.example.chatapp.model.dto.RoomDTO;

import java.util.List;

public class DevTools {

    public static Long getLastMessageID(List<Message> messages) {
        return messages.get(messages.size() - 1).getId();
    }

    public static MessageDTO messageToDTO(Message message) {
        return new MessageDTO(
                message.getSender(),
                message.getContent(),
                message.getTimestamp()
        );
    }

    public static RoomDTO roomsToDTO(Room room) {
        return new RoomDTO(room.getName());
    }
}
