package com.example.chatapp.handler.exception;

public class TopicUniqueException extends RuntimeException {
    public TopicUniqueException(String message) {
        super(message);
    }
}
