package com.example.chatapp;

import java.security.SecureRandom;

public class Test {
    private static final String CHARACTERS = "0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    public static void main(String[] args) {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            System.out.println(code);
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(100000 + random.nextInt(900000));
        }
    }
}
