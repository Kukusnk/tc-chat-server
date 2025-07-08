package com.example.chatapp.util;

import com.example.chatapp.model.Message;
import com.example.chatapp.model.dto.MessageDTO;

import java.util.List;

public class DevTools {

    public static Long getLastMessageID(List<Message> messages) {
        return messages.get(messages.size() - 1).getId();
    }

    public static MessageDTO messageToDTO(Message message) {
        return new MessageDTO(
                message.getSender().getNickname(),
                message.getContent(),
                message.getTimestamp()
        );
    }
}
