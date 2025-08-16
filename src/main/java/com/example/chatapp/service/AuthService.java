package com.example.chatapp.service;

import com.example.chatapp.handler.exception.UserEmailException;
import com.example.chatapp.handler.exception.UserUsernameException;
import com.example.chatapp.model.RefreshToken;
import com.example.chatapp.model.User;
import com.example.chatapp.model.dto.auth.AuthResponse;
import com.example.chatapp.model.dto.auth.LoginRequest;
import com.example.chatapp.model.dto.auth.RegisterRequest;
import com.example.chatapp.model.dto.refresh_token.RefreshTokenRequest;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final static String EMAIL_REGEXP = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            log.error("Username already exists");
            throw new UserUsernameException("User with username '" + request.getUsername() + "' already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Email already exists");
            throw new UserEmailException("User with email '" + request.getEmail() + "' already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName("First Name")
                .lastName("Last Name")
                .createdAt(LocalDate.now())
                .isEmailVerified(false)
                .build();

        user = userRepository.save(user);

        String accessToken = jwtUtil.generateToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user;
        if (request.getUsernameOrEmail().matches(EMAIL_REGEXP)) {
            user = userRepository.findByEmail(request.getUsernameOrEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials, email does not exist"));
        } else {
            user = userRepository.findByUsername(request.getUsernameOrEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials, username does not exist"));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials, password does not match");
        }

        String accessToken = jwtUtil.generateToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtUtil.generateToken(user.getUsername());
                    return AuthResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(request.getRefreshToken())
                            .tokenType("Bearer")
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database"));
    }

    public void logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenService.deleteByUser(user);
    }
}
