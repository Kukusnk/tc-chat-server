package com.example.chatapp.service;

import com.example.chatapp.handler.exception.MessageEmptyException;
import com.example.chatapp.handler.exception.RoomNotFoundException;
import com.example.chatapp.model.Message;
import com.example.chatapp.model.Room;
import com.example.chatapp.model.dto.message.MessageDTO;
import com.example.chatapp.model.dto.message.SendMessageDTO;
import com.example.chatapp.repository.MessageRepository;
import com.example.chatapp.repository.RoomRepository;
import com.example.chatapp.util.DevTools;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, RoomRepository roomRepository) {
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
    }

    public List<MessageDTO> getAllMessagesByRoomId(Long roomId) {
        logger.info("Get all messages by room id: {}", roomId);
        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException("Room not found");
        }
        List<Message> messages = messageRepository.findByRoom_Id(roomId).isEmpty() ?
                new ArrayList<>() : messageRepository.findByRoom_Id(roomId);

        return messages.stream().map(DevTools::messageToDTO).collect(Collectors.toList());
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

    public MessageDTO saveAndReturn(Long roomId, SendMessageDTO request) {
        if (request.getContent() == null) {
            logger.info("Message is empty: {}", request);
            throw new MessageEmptyException("Message content is empty");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        Message message = Message.builder()
                .sender(request.getSender())
                .content(request.getContent())
                .timestamp(LocalDateTime.now())
                .room(room)
                .build();

        messageRepository.save(message);
        logger.info("Save message: {}", message.getContent());

        return MessageDTO.builder()
                .sender(message.getSender())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .build();
    }


}
