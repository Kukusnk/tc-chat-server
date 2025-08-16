package com.example.chatapp.model.dto.email_verification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyResetCodeRequest {

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Код не может быть пустым")
    @Pattern(regexp = "\\d{6}", message = "Код должен содержать 6 цифр")
    private String code;
}
