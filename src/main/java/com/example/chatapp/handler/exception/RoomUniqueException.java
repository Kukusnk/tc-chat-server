package com.example.chatapp.handler.exception;

@Deprecated
public class RoomUniqueException extends RuntimeException {
    public RoomUniqueException(String message) {
        super(message);
    }
}
