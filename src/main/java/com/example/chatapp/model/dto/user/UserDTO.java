package com.example.chatapp.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    @NotBlank
    @Max(value = 15)
    @Min(value = 6)
    String username;
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    String email;
    @NotBlank
    @Min(value = 8)
    String password;
    String firstName;
    String lastName;
    LocalDate createdAt;
    Boolean isEmailVerified;
}
