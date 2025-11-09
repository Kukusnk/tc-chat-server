package com.example.chatapp;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

public class Test {
    private static final String CHARACTERS = "0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();
    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static void main(String[] args) {
//        StringBuilder code = new StringBuilder(CODE_LENGTH);
//        for (int i = 0; i < CODE_LENGTH; i++) {
//            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
//            System.out.println(code);
//        }
//        for (int i = 0; i < 10; i++) {
//            System.out.println(100000 + random.nextInt(900000));
//        }
        //System.out.println(passwordEncoder.encode("$2a$10$KnVjJH41M0tyh/ZVnHHf2.QSRuNNleVZVPEDNRg7Jj2MK7TjyDufy"));
        String password = "FirstUser";
        System.out.println(passwordEncoder.encode(password));

    }
}
