package com.example.chatapp.handler.exception;

public class RoomUniqueException extends RuntimeException {
    public RoomUniqueException(String message) {
        super(message);
    }
}
