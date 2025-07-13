package com.example.chatapp.service;

import com.example.chatapp.handler.exception.MessageEmptyException;
import com.example.chatapp.model.Message;
import com.example.chatapp.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllMessagesByRoomId(Long roomId) {
        logger.info("Get all messages by room id: {}", roomId);
        return messageRepository.findByRoom_Id(roomId).isEmpty() ?
                new ArrayList<>() : messageRepository.findByRoom_Id(roomId);
    }

    public Message saveMessage(Message message) {
        if (message.getId() == null) {
            logger.info("Message is empty: {}", message);
            throw new MessageEmptyException("Message content is empty");
        }
        logger.info("Save message: {}", message.getContent());
        return messageRepository.save(message);
    }


}
