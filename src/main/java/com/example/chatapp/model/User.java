package com.example.chatapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotBlank
    @Size(min = 4, max = 16, message = "Username must be between 4 and 16")
    @Column(unique = true)
    String username;
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    @Column(unique = true)
    String email;
    @NotBlank
    @Size(min = 8, message = "Password must be greater than or equal to 8")
    String password;

    @Column(name = "avatar_url")
    private String avatarUrl;
    LocalDate createdAt;
    Boolean isEmailVerified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role;

    public enum Role {
        ADMIN,
        USER
    }
}

