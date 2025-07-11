package com.example.chatapp.handler.exception;

public class MessageEmptyException extends RuntimeException {
    public MessageEmptyException(String message) {
        super(message);
    }
}
