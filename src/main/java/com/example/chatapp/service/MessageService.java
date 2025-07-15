package com.example.chatapp.service;

import com.example.chatapp.handler.exception.MessageEmptyException;
import com.example.chatapp.model.Message;
import com.example.chatapp.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

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

    public String answerMessage(String message) {
        StringBuilder answerMessage = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            int index = ThreadLocalRandom.current().nextInt(characters.length());
            answerMessage.append(characters.charAt(index));
        }
        logger.info("Answer message created");

        return answerMessage.toString();
    }


}
