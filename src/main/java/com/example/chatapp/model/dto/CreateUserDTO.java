package com.example.chatapp.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserDTO {
    @NotBlank(message = "Username cannot be null or empty")
    @Max(value = 15)
    @Min(value = 6)
    String username;
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email should be valid")
    String email;
    @NotBlank(message = "Password cannot be null or empty")
    @Min(value = 8)
    String password;
}
