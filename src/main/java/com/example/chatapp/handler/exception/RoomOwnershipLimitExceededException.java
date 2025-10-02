package com.example.chatapp.handler.exception;

public class RoomOwnershipLimitExceededException extends RuntimeException {
    public RoomOwnershipLimitExceededException(String message) {
        super(message);
    }
}
