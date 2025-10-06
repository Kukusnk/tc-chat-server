package com.example.chatapp.handler.exception;

public class RoomLimitMembersException extends RuntimeException {
    public RoomLimitMembersException(String message) {
        super(message);
    }
}
