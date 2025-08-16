package com.example.chatapp.service;

import com.example.chatapp.handler.exception.UserNotFoundException;
import com.example.chatapp.handler.exception.VerificationException;
import com.example.chatapp.model.User;
import com.example.chatapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void verifiedUserByEmail(String email) {
        User user = getUserByEmailOrThrow(email);
        if (!user.getIsEmailVerified()) {
            user.setIsEmailVerified(true);
        } else {
            log.info("User already verified");
            throw new VerificationException("Email " + email + " already verified");
        }
        userRepository.save(user);
        log.info("User verified");
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email '" + email + "' not found"));
    }

    public User getUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));
    }

    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public void updatePasswordByEmail(String email, String encodedPassword) {
        userRepository.updatePasswordByEmail(email, encodedPassword);
    }
}
