package com.example.chatapp.service;

import com.example.chatapp.handler.exception.UserNotFoundException;
import com.example.chatapp.model.User;
import com.example.chatapp.repository.UserRepository;
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

//    public void createUser(CreateUserDTO userDTO) {
//
//        if (userRepository.existsByUsername(userDTO.getUsername())) {
//            log.error("Username already exists");
//            throw new UserUsernameException("User with username '" + userDTO.getUsername() + "' already exists");
//        }
//
//        if (userRepository.existsByEmail(userDTO.getEmail())) {
//            log.error("Email already exists");
//            throw new UserEmailException("User with email '" + userDTO.getEmail() + "' already exists");
//        }
//
//        User user = User.builder()
//                .username(userDTO.getUsername())
//                .email(userDTO.getEmail())
//                .password(passwordEncoder.encode(userDTO.getPassword()))
//                .firstName("First Name")
//                .lastName("Last Name")
//                .createdAt(LocalDate.now())
//                .isEmailVerified(false)
//                .build();
//
//        userRepository.save(user);
//        log.info("User created: {}", userDTO.getUsername());
//    }

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

    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

}
