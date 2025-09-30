package com.example.chatapp.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class Chat {
    //TODO entity class for random match chat and one-on-one direct chat
    @Enumerated(EnumType.STRING)
    private ChatType type;
}
