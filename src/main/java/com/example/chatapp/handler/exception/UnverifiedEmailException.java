package com.example.chatapp.handler.exception;

public class UnverifiedEmailException extends RuntimeException {
    public UnverifiedEmailException(String message) {
        super(message);
    }
}
