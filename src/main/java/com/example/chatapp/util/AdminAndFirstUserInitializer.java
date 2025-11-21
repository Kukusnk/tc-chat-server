package com.example.chatapp.util;

import com.example.chatapp.handler.exception.RoleNotFoundException;
import com.example.chatapp.model.Role;
import com.example.chatapp.model.User;
import com.example.chatapp.repository.RoleRepository;
import com.example.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAndFirstUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${adminInitializer.adminName}")
    private final String adminName;
    @Value("${adminInitializer.adminEmail}")
    private final String adminEmail;
    @Value("${adminInitializer.adminPassword}")
    private final String adminPassword;
    @Value("${adminInitializer.userName}")
    private final String userName;
    @Value("${adminInitializer.userEmail}")
    private final String userEmail;
    @Value("${adminInitializer.userPassword}")
    private final String userPassword;

    @Override
    public void run(String... args) {
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        if (userRepository.findByUsername(adminName).isPresent() || userRepository.findByEmail(adminEmail).isPresent()) {
            log.info("Admin user already exists, skipping creation.");
        } else {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RoleNotFoundException("Role ADMIN not found"));


            User admin = User.builder()
                    .username(adminName)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .avatarUrl(null)
                    .createdAt(LocalDate.now())
                    .isEmailVerified(true)
                    .roles(Set.of(adminRole, userRole))
                    .build();

            userRepository.save(admin);
            log.info("Default admin user created: {} / {}", admin.getUsername(), admin.getEmail());
        }

        if (userRepository.findByUsername(userName).isPresent() || userRepository.findByEmail(userEmail).isPresent()) {
            log.info("User already exists, skipping creation.");
        } else {
            User user = User.builder()
                    .username(userName)
                    .email(userEmail)
                    .password(passwordEncoder.encode(userPassword))
                    .avatarUrl(null)
                    .createdAt(LocalDate.now())
                    .isEmailVerified(true)
                    .roles(Set.of(userRole))
                    .build();

            userRepository.save(user);
            log.info("Default user created: {} / {}", user.getUsername(), user.getEmail());
        }
    }
}
